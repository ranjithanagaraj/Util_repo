def function(props) {
	stage('CheckoutProject') {
		app_url =props.JAVA_APP_REPO_GIT_URL		
		git "${app_url}"
		pom = readMavenPom file: props.POM_FILE
		artifactId=pom.artifactId
		echo "${artifactId}"
		version=pom.version
		
	}
	stage('SonarAnalysis')
	{
	commonUtility.sonar();
	}
	stage("SonarQube Quality Gate")
	{
	commonUtility.qualityGate();
	}
	stage('BuildProject') 
	{
	sh props.MAVEN_BUILD		
   	}
	stage('UploadArtifactory') {
	commonUtility.uploadArtifact();
	}
	stage('downloadingArtifact')
	{
	commonUtility.downloadArtifact();	
	}
	stage('Build & Push Docker image')
	{
	  sh props.DOCKER_BUILD
	  sh props.DOCKER_PUSH
	}	
	stage('Dev deploy') {
	def Deploy = false;
	try {
		echo "Deploy To Dev"
		input message: 'Deploy?', ok: 'Deploy'
		Deploy = true
		} catch (err) {
	Deploy = false
	}
	if(Deploy)
	{
    	echo 'Docker Deploy'
         sh props.DOCKER_CMD
	 sh props.DOCKER_TAG
  	 sh props.DOCKER_RUN
	}
	}
	stage('Prod Deploy') {
	def Deploy = false;
	try {
		echo "Deploy To Prod"
		input message: 'Deploy?', ok: 'Deploy'
		Deploy = true
		} catch (err) {
	Deploy = false
	}
	if(Deploy)
	{
    	echo 'Deploy to kubernetes'
     	sh props.KUBERNETES_APPLY
     	sh props.KUBERNETES_GET_ALL
	}
	}
	
	stage('Email Notification')
	{
		commonUtility.sendEmail();
	}
	}
return this
