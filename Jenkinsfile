pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'echo "This is build stage"'
                sh '''
                    echo "Multiline shell steps works too"
                    ls -lah
                '''
            }
        }
        stage('Test') {
            steps {
                sh 'echo "This is test stage"'
                sh '''
                    echo "Multiline shell steps works too"
                    echo ${BUILD_NUMBER}
                    echo ${BUILD_TAG}
                    echo ${NODE_NAME}
                '''
            }
        }
    }
}