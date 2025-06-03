// product-service/Jenkinsfile
pipeline {
    agent any
    
    tools {
        maven 'maven-3'
        jdk 'jdk-21'
    }
    
    environment {
        SERVICE = 'account'
        NAME = "joaopgs4/${env.SERVICE}"
        REGISTRY_CREDENTIALS = 'dockerhub-credentials'
    }
    
    stages {
        stage('Checkout Dependencies') {
            steps {
                script {
                    dir('libs/account') {
                        git branch: 'main', url: 'https://github.com/joaopgs4/account.git'
                        sh 'mvn clean install -DskipTests'
                    }
                    
                    dir('libs/auth') {
                        git branch: 'main', url: 'https://github.com/joaopgs4/account-service.git'
                        sh 'mvn clean install -DskipTests'
                    }
                    dir('libs/product') {
                        git branch: 'main', url: 'https://github.com/joaopgs4/product.git'
                        sh 'mvn clean install -DskipTests'
                    }
                }
            }
        }
        
        stage('Build') { 
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }      
        
        stage('Build & Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: env.REGISTRY_CREDENTIALS, 
                                                   usernameVariable: 'USERNAME', 
                                                   passwordVariable: 'TOKEN')]) {
                        sh """
                            docker login -u \$USERNAME -p \$TOKEN
                            docker build -t ${env.NAME}:latest .
                            docker build -t ${env.NAME}:${env.BUILD_NUMBER} .
                            docker push ${env.NAME}:latest
                            docker push ${env.NAME}:${env.BUILD_NUMBER}
                        """
                    }
                }
            }
        }
    }
}