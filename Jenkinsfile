pipeline {
    agent any
    stages {
        stage('Shopizer CI') {
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
    }
}