pipeline {
    agent any
    stages {
        stage('Install docker') {
            steps {
                sh '''
                    curl -fsSLO https://get.docker.com/builds/Linux/x86_64/docker-17.04.0-ce.tgz \
                      && tar xzvf docker-17.04.0-ce.tgz \
                      && mv docker/docker /usr/local/bin \
                      && rm -r docker docker-17.04.0-ce.tgz
                '''
            }
        }
        stage('Shopizer Test') {
            agent {
                docker {
                    image 'shopizerecomm/ci:java11'
                }
            }
            steps {
                sh 'echo "shopizer build and test"'
                sh '''
                    pwd
                    set -x
                    /home/shopizer/tools/shopizer.sh tests
                '''
            }
        }
        stage('Build image') {
            steps {
                sh '''
                    cd sm-shop
                    docker build . -t sinhblue/shopizer:$BUILD_NUMBER
                '''
            }
        }
        stage('Push image') {
            steps {
                sh '''
                    echo "hoilamgi@287" | docker login --username "sinhblue" --password-stdin
                    docker push sinhblue/shopizer:$BUILD_NUMBER
                '''
            }
        }
    }
}