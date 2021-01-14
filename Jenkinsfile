pipeline {
    agent any
    tools { 
        maven 'maven-3.6.3' 
        dockerTool 'docker'
    }
    stages {
        stage('Test') { 
            steps {
                sh "mvn clean test"
            }
        }
        stage('Package') { 
            steps {
                sh "mvn clean package -Dmaven.test.skip=true"
            }
        }
       stage('Build Image') { 
            steps {
               sh './build-image.sh'
            }
        }
        stage('Push Image') { 
            steps {
                sh 'docker login -u=williamdrew -p=Kankakee01'
                sh './push-image.sh' 
            }
        }
        stage('Verification') { 
            steps {
                sh "mvn clean verify -Pintegration-tests -Psec-scan -Psonar sonar:sonar -Dk8s-provider=aws"
            }
        }

    }
    post { 
        always { 
            deleteDir()
        }
    }
}

