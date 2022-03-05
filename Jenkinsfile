pipeline {
    agent {
        docker {
            image 'shopizerecomm/ci:java11'
        }
    }
    stages {
        stage('Shopizer CI') {
            steps {
                sh '''
                    export CIRCLE_WORKING_DIRECTORY=/tmp
                    export HOME=/home/$(whoami)
                '''
                dir($CIRCLE_WORKING_DIRECTORY) {
                    checkout scm
                    sh 'echo "shopizer build and test"'
                    sh '''
                        export CIRCLE_WORKING_DIRECTORY=/tmp
                        export HOME=/home/$(whoami)
                        pwd
                        ls -lah
                        set -x
                        /home/shopizer/tools/shopizer.sh tests
                    '''
                }
            }
        }
    }
}
