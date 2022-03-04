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
                sh 'echo "shopizer build and test"'
                sh '''
                    export CIRCLE_WORKING_DIRECTORY=/tmp
                    pwd
                    ls -lah
                    set -x
                    /home/shopizer/tools/shopizer.sh tests
                '''
            }
        }
    }
}
