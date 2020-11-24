import hudson.tasks.test.AbstractTestResultAction

properties([
  [
    $class: 'ThrottleJobProperty',
    categories: ['pipeline'],
    throttleEnabled: true,
    throttleOption: 'category'
  ]
])
pipeline {
    agent any
    options {
        buildDiscarder(logRotator(
            numToKeepStr: env.BRANCH_NAME.equals("master") ? '15' : '3',
            daysToKeepStr: env.BRANCH_NAME.equals("master") || env.BRANCH_NAME.startsWith("rel-") ? '' : '7',
            artifactDaysToKeepStr: env.BRANCH_NAME.equals("master") || env.BRANCH_NAME.startsWith("rel-") ? '' : '3',
            artifactNumToKeepStr: env.BRANCH_NAME.equals("master") || env.BRANCH_NAME.startsWith("rel-") ? '' : '1'
        ))
        disableConcurrentBuilds()
        skipStagesAfterUnstable()
    }
    environment {
        PATH = "/usr/local/bin/:$PATH"
        COMPOSE_PROJECT_NAME = "diagnostics-${BRANCH_NAME}"
    }
    parameters {
        string(name: 'contractTestsBranch', defaultValue: 'master', description: 'The branch of contract tests to checkout')
    }
    stages {
        stage('Preparation') {
            steps {
                checkout scm

                withCredentials([usernamePassword(
                  credentialsId: "cad2f741-7b1e-4ddd-b5ca-2959d40f62c2",
                  usernameVariable: "USER",
                  passwordVariable: "PASS"
                )]) {
                    sh 'set +x'
                    sh 'docker login -u $USER -p $PASS'
                }
                script {
                    def properties = readProperties file: 'gradle.properties'
                    if (!properties.serviceVersion) {
                        error("serviceVersion property not found")
                    }
                    VERSION = properties.serviceVersion
                    STAGING_VERSION = properties.serviceVersion
                    if (env.GIT_BRANCH != 'master' || (env.GIT_BRANCH == 'master' && !VERSION.endsWith("SNAPSHOT"))) {
                        STAGING_VERSION += "-STAGING"
                    }
                    currentBuild.displayName += " - " + VERSION
                }
            }
            post {
                failure {
                    script {
                        notifyAfterFailure()
                    }
                }
            }
        }
        stage('Build') {
            steps {
                withCredentials([file(credentialsId: '8da5ba56-8ebb-4a6a-bdb5-43c9d0efb120', variable: 'ENV_FILE')]) {
                    script {
                        try {
                            sh 'set +x'
                            sh 'sudo rm -f .env'
                            sh 'cp $ENV_FILE .env'
                            sh '''
                                if [ "$GIT_BRANCH" != "master" ]; then
                                    sed -i '' -e "s#^TRANSIFEX_PUSH=.*#TRANSIFEX_PUSH=false#" .env  2>/dev/null || true
                                fi
                            '''

                            sh 'docker-compose -f docker-compose.builder.yml run -e BUILD_NUMBER=$BUILD_NUMBER -e GIT_BRANCH=$GIT_BRANCH builder'
                            sh 'docker-compose -f docker-compose.builder.yml build image'
                            sh 'docker-compose -f docker-compose.builder.yml down --volumes'
                            sh "docker tag openlmis/diagnostics:latest openlmis/diagnostics:${STAGING_VERSION}"
                            sh "docker push openlmis/diagnostics:${STAGING_VERSION}"
                            currentBuild.result = processTestResults('SUCCESS')
                        }
                        catch (exc) {
                            currentBuild.result = processTestResults('FAILURE')
                            if (currentBuild.result == 'FAILURE') {
                                error(exc.toString())
                            }
                        }
                    }
                }
            }
            post {
                success {
                    archive 'build/libs/*.jar,build/resources/main/api-definition.html, build/resources/main/  version.properties'
                }
                unstable {
                    script {
                        notifyAfterFailure()
                    }
                }
                failure {
                    script {
                        notifyAfterFailure()
                    }
                }
            }
        }
        stage('Deploy to test') {
            when {
                expression {
                    return env.GIT_BRANCH == 'master' && VERSION.endsWith("SNAPSHOT")
                }
            }
            steps {
                build job: 'OpenLMIS-diagnostics-deploy-to-test', wait: false
            }
            post {
                failure {
                    script {
                        notifyAfterFailure()
                    }
                }
            }
        }
        stage('Parallel: Sonar analysis and contract tests') {
            when {
                expression {
                    return VERSION.endsWith("SNAPSHOT")
                }
            }
            parallel {
                stage('Sonar analysis') {
                    steps {
                        withSonarQubeEnv('Sonar OpenLMIS') {
                            withCredentials([string(credentialsId: 'SONAR_LOGIN', variable: 'SONAR_LOGIN'), string(credentialsId: 'SONAR_PASSWORD', variable: 'SONAR_PASSWORD')]) {
                                script {
                                    sh '''
                                        set +x
                                        sudo rm -f .env

                                        curl -o .env -L https://raw.githubusercontent.com/OpenLMIS/openlmis-ref-distro/master/settings-sample.env

                                        sed -i '' -e "s#spring_profiles_active=.*#spring_profiles_active=#" .env  2>/dev/null || true
                                        sed -i '' -e "s#^BASE_URL=.*#BASE_URL=http://localhost#" .env  2>/dev/null || true
                                        sed -i '' -e "s#^VIRTUAL_HOST=.*#VIRTUAL_HOST=localhost#" .env  2>/dev/null || true

                                        SONAR_LOGIN_TEMP=$(echo $SONAR_LOGIN | cut -f2 -d=)
                                        SONAR_PASSWORD_TEMP=$(echo $SONAR_PASSWORD | cut -f2 -d=)
                                        echo "SONAR_LOGIN=$SONAR_LOGIN_TEMP" >> .env
                                        echo "SONAR_PASSWORD=$SONAR_PASSWORD_TEMP" >> .env
                                        echo "SONAR_BRANCH=$GIT_BRANCH" >> .env

                                        docker-compose -f docker-compose.builder.yml run sonar
                                        docker-compose -f docker-compose.builder.yml down --volumes

                                        sudo rm -vrf .env
                                    '''
                                    // workaround: Sonar plugin retrieves the path directly from the output
                                    sh 'echo "Working dir: ${WORKSPACE}/build/sonar"'
                                }
                            }
                        }
                        timeout(time: 1, unit: 'HOURS') {
                            script {
                                def gate = waitForQualityGate()
                                if (gate.status != 'OK') {
                                    echo 'Quality Gate FAILED'
                                    currentBuild.result = 'UNSTABLE'
                                }
                            }
                        }
                    }
                    post {
                        unstable {
                            script {
                                notifyAfterFailure()
                            }
                        }
                        failure {
                            script {
                                notifyAfterFailure()
                            }
                        }
                    }
                }
                stage('Contract tests') {
                    steps {
                        build job: "OpenLMIS-contract-tests-pipeline/${params.contractTestsBranch}", propagate: true, wait: true,
                        parameters: [
                            string(name: 'serviceName', value: 'diagnostics'),
                            text(name: 'customEnv', value: "OL_DIAGNOSTICS_VERSION=${STAGING_VERSION}")
                        ]
                    }
                    post {
                        failure {
                            script {
                                notifyAfterFailure()
                            }
                        }
                    }
                }
            }
        }
        stage('Push image') {
            when {
                expression {
                    env.GIT_BRANCH =~ /rel-.+/ || (env.GIT_BRANCH == 'master' && !VERSION.endsWith("SNAPSHOT"))
                }
            }
            steps {
                sh "docker pull openlmis/diagnostics:${STAGING_VERSION}"
                sh "docker tag openlmis/diagnostics:${STAGING_VERSION} openlmis/diagnostics:${VERSION}"
                sh "docker push openlmis/diagnostics:${VERSION}"
            }
            post {
                success {
                    script {
                        if (!VERSION.endsWith("SNAPSHOT")) {
                            currentBuild.setKeepLog(true)
                        }
                    }
                }
                failure {
                    script {
                        notifyAfterFailure()
                    }
                }
            }
        }
    }
    post {
        fixed {
            script {
                BRANCH = "${BRANCH_NAME}"
                if (BRANCH.equals("master") || BRANCH.startsWith("rel-")) {
                    slackSend color: 'good', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Back to normal"
                }
            }
        }
    }
}

def notifyAfterFailure() {
    messageColor = 'danger'
    if (currentBuild.result == 'UNSTABLE') {
        messageColor = 'warning'
    }
    BRANCH = "${BRANCH_NAME}"
    if (BRANCH.equals("master") || BRANCH.startsWith("rel-")) {
        slackSend color: "${messageColor}", message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} ${env.STAGE_NAME} ${currentBuild.result} (<${env.BUILD_URL}|Open>)"
    }
    emailext subject: "${env.JOB_NAME} - #${env.BUILD_NUMBER} ${env.STAGE_NAME} ${currentBuild.result}",
        body: """<p>${env.JOB_NAME} - #${env.BUILD_NUMBER} ${env.STAGE_NAME} ${currentBuild.result}</p><p>Check console <a href="${env.BUILD_URL}">output</a> to view the results.</p>""",
        recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']]
}

def processTestResults(status) {
    checkstyle pattern: '**/build/reports/checkstyle/*.xml'
    pmd pattern: '**/build/reports/pmd/*.xml'
    junit '**/build/test-results/*/*.xml'

    AbstractTestResultAction testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
    if (testResultAction != null) {
        failuresCount = testResultAction.failCount
        echo "Failed tests count: ${failuresCount}"
        if (failuresCount > 0) {
            echo "Setting build unstable due to test failures"
            status = 'UNSTABLE'
        }
    }

    return status
}
