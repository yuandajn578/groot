pipeline {
  agent any
  options { skipDefaultCheckout() }
  stages {
    stage('Git Parameter') {
      steps {
        git branch: "${params.BRANCH}", url: 'git@gitlab.choicesoft.com.cn:architect/groot.git'
      }
    }
    stage('Build Project') {
      steps {
        script{
          if ("${deploy_env}" == "deploy") {
          sh 'mvn -U clean package -Dmaven.test.skip=true'
          sh 'echo "JOB_NAME=${JOB_NAME}"'
          sh 'echo "JOB_BASE_NAME=${JOB_BASE_NAME}"'
          }
        }
      }
    }
    stage('Build Docker Image') {
      steps {
        script{
          if ("${deploy_env}" == "deploy") {
            sh 'docker build --build-arg env=${build_env} --build-arg idc=${build_idc}  -t ${tag} .'
          }
        }
      }
    }
    stage('Push image') {
      steps {
        script{
          if ("${deploy_env}" == "deploy") {
            sh 'docker push ${tag}'
          }
        }
      }
    }
    stage('Deploy to enviroment') {
      steps {
        script{
          if ("${deploy_env}" == "deploy") {
            sh 'sed -i "s*myimage*${tag}*" kubernetes.yml'
            sh 'sed -i "s*mynamespace*${namespace}*" kubernetes.yml'
            sh 'sed -i "s*appname*${appname}*" kubernetes.yml'
            sh 'sed -i "s*java_opts*${JAVA_OPTS}*" kubernetes.yml'
            archiveArtifacts artifacts: 'kubernetes.yml', fingerprint: true
            sh '/usr/local/bin/kubectl --context newrancher apply --namespace=${namespace} -f kubernetes.yml'
          }
          currentBuild.description = "#[${deploy_env}]--[${build_env}]--[${BRANCH}]"
        }
      }
    }
    stage('回滚到指定版本') {
      steps{
        script{
          if ("${deploy_env}" == "rollback") {
            echo "回滚到指定版本,版本号为${BUILDNUMBER}"
              sh '/usr/local/bin/kubectl --context newrancher apply --namespace=${namespace} -f /opt/jenkins/jobs/${JOB_NAME}/builds/${BUILDNUMBER}/archive/kubernetes.yml'
          }
        }
      }
    }
  }
  environment {
    //修改appname
    appname = "groot-${params.build_idc}"
    //修改对应项目名称 如 c7p scm report
    project = 'c7p'
    namespace = "${project}-${params.build_env}"
    tag = "r.cn/${namespace}/${appname}:${env.BUILD_NUMBER}"
    build_env = "${params.build_env}"
    build_idc = "${params.build_idc}"
    deploy_env = "${params.deploy_env}"
    JAVA_OPTS = "-server -Xms1024m -Xmx1024m -Xmn1024m -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=512m"
  }
  parameters {
    choice(name: 'deploy_env', choices: ['deploy','rollback'], description: 'deploy: 发布  rollback: 回滚')
    string(name: 'BUILDNUMBER', defaultValue: '0', description: '构建历史版本号,回滚时填写')
    choice(name: 'build_env', choices: ['fat','dev','ttt','pre','tsm'], description: '将应用部署到以下环境')
    choice(name: 'build_idc', choices: ['default','tsm','local','npe'], description: '将应用部署到以下环境idc')
    gitParameter(name: 'BRANCH', defaultValue: 'master', branchFilter: 'origin/(.*)', type: 'PT_BRANCH', listSize: '15')
  }
}
