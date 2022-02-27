pipeline {
    agent any
    stages {
        stage('Initialize'){
            def dockerHome = tool 'myDocker'
            env.PATH = "${dockerHome}/bin:${env.PATH}"
        }
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