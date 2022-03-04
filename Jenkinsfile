pipeline {
    agent {
        docker {
            image 'shopizerecomm/ci:java11'
        }
    }
    stages {
        stage('Shopizer CI') {
            steps {
                checkout scm
                export CIRCLE_WORKING_DIRECTORY=/tmp
                sh 'echo "shopizer build and test"'
                sh '''
                    pwd
                    ls -lah
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
