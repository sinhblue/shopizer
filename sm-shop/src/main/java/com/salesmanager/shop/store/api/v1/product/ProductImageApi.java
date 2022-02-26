package com.salesmanager.shop.store.api.v1.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.image.ProductImageService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.entity.NameEntity;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.api.exception.UnauthorizedException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/api/v1")
@Api(tags = { "Manage product images. Add, remove and set the order of product images." })
@SwaggerDefinition(tags = {
		@Tag(name = "Product images management", description = "Add and remove products images. Change images sort order.") })
public class ProductImageApi {

	@Inject
	private ProductImageService productImageService;

	@Inject
	private ProductService productService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductImageApi.class);

	/**
	 * To be used with MultipartFile
	 *
	 * @param id
	 * @param uploadfiles
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(value = { "/private/products/{id}/images", "/auth/products/{id}/images" }, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE }, method = RequestMethod.POST)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public void uploadImage(@PathVariable Long id, @RequestParam(value = "file", required = true) MultipartFile[] files,
			@RequestParam(value = "order", required = false, defaultValue = "0") Integer position,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) throws IOException {

		try {

			// get the product
			Product product = productService.getById(id);
			if (product == null) {
				throw new ResourceNotFoundException("Product not found");
			}

			// security validation
			// product belongs to merchant store
			if (product.getMerchantStore().getId().intValue() != merchantStore.getId().intValue()) {
				throw new UnauthorizedException("Resource not authorized for this merchant");
			}

			boolean hasDefaultImage = false;
			Set<ProductImage> images = product.getImages();
			if (!CollectionUtils.isEmpty(images)) {
				for (ProductImage image : images) {
					if (image.isDefaultImage()) {
						hasDefaultImage = true;
						break;
					}
				}
			}

			List<ProductImage> contentImagesList = new ArrayList<ProductImage>();
			int sortOrder = position;
			for (MultipartFile multipartFile : files) {
				if (!multipartFile.isEmpty()) {
					ProductImage productImage = new ProductImage();
					productImage.setImage(multipartFile.getInputStream());
					productImage.setProductImage(multipartFile.getOriginalFilename());
					productImage.setProduct(product);

					if (!hasDefaultImage) {
						productImage.setDefaultImage(true);
						hasDefaultImage = true;
					}
					productImage.setSortOrder(sortOrder);

					position++;

					contentImagesList.add(productImage);
				}
			}

			if (CollectionUtils.isNotEmpty(contentImagesList)) {
				productImageService.addProductImages(product, contentImagesList);
			}

		} catch (Exception e) {
			LOGGER.error("Error while creating ProductImage", e);
			throw new ServiceRuntimeException("Error while creating image");
		}
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = { "/private/products/images/{id}",
			"/auth/products/images/{id}" }, method = RequestMethod.DELETE)
	public void deleteImage(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try {
			ProductImage productImage = productImageService.getById(id);

			if (productImage != null) {
				productImageService.delete(productImage);
			} else {
				response.sendError(404, "No ProductImage found for ID : " + id);
			}

		} catch (Exception e) {
			LOGGER.error("Error while deleting ProductImage", e);
			try {
				response.sendError(503, "Error while deleting ProductImage " + e.getMessage());
			} catch (Exception ignore) {
			}
		}
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = { "/private/products/{id}/image/{imageId}" }, method = RequestMethod.DELETE)
	public void deleteImage(@PathVariable Long id, @PathVariable Long imageId, @Valid NameEntity imageName,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {

		try {
			Optional<ProductImage> productImage = productImageService.getProductImage(imageId, id, merchantStore);

			if (productImage.isPresent()) {
				productImageService.delete(productImage.get());
			} else {
				throw new ResourceNotFoundException("Product image [" + imageName.getName()
						+ "] not found for product id [" + id + "] and merchant [" + merchantStore.getCode() + "]");
			}

		} catch (Exception e) {
			LOGGER.error("Error while deleting ProductImage", e);
			throw new ServiceRuntimeException("ProductImage [" + imageName.getName() + "] cannot be deleted");
		}
	}

	/**
	 * 
	 * Patch image (change position)
	 * 
	 * @param id
	 * @param files
	 * @param position
	 * @param merchantStore
	 * @param language
	 * @throws IOException
	 */

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = { "/private/products/{id}/image/{imageId}",
			"/auth/products/{id}/image/{id}" }, method = RequestMethod.PATCH)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public void imageDetails(@PathVariable Long id, @PathVariable Long imageId,
			@RequestParam(value = "order", required = false, defaultValue = "0") Integer position,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) throws IOException {

		try {
			
			Product p = productService.getById(id);
			
			if(p==null) {
				throw new ResourceNotFoundException("Product image [" + imageId + "] not found for product id [" + id
						+ "] and merchant [" + merchantStore.getCode() + "]");
			}
			
			if(p.getMerchantStore().getId() != merchantStore.getId()) {
				throw new ResourceNotFoundException("Product image [" + imageId + "] not found for product id [" + id
						+ "] and merchant [" + merchantStore.getCode() + "]");
			}
			
			Optional<ProductImage> productImage = productImageService.getProductImage(imageId, id, merchantStore);

			if (productImage.isPresent()) {
				productImage.get().setSortOrder(position);
				productImageService.updateProductImage(p, productImage.get());
			} else {
				throw new ResourceNotFoundException("Product image [" + imageId + "] not found for product id [" + id
						+ "] and merchant [" + merchantStore.getCode() + "]");
			}
			
			

		} catch (Exception e) {
			LOGGER.error("Error while deleting ProductImage", e);
			throw new ServiceRuntimeException("ProductImage [" + imageId + "] cannot be edited");
		}
	}

}
