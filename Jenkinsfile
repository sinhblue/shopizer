pipeline {
    agent any
    stages {
        stage('Build image') {
            steps {
                sh '''
                    echo "cd sm-shop && docker build . -t $DOCKER_ID/shopizer:$BUILD_NUMBER"
                '''
            }
        }
        stage('Push image') {
            steps {
                sh '''
                    echo "$DOCKER_PASSWORD" | docker login --username "$DOCKER_ID" --password-stdin
                    docker push $DOCKER_ID/shopizer:$BUILD_NUMBER
                '''
            }
        }
    }
}