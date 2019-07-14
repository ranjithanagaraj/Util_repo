def uploadWarArtifactory() {
	script {
		def server = Artifactory.server 'artifactory'
               def uploadSpec = """{
   	
                "files":[
                    {
			"pattern":"/var/lib/jenkins/workspace/example/target/*.jar",

			"target":"art1/${artifactId}/${version}.${BUILD_NUMBER}/"
			}]
		}"""
		server.upload(uploadSpec) 	
	}
}

def sonar(){
	withSonarQubeEnv('sonarqube') {
	def mvncmd=props.SONAR_SCAN
	sh "${mvncmd}"
	}
}

def sendEmail() {
		 emailext body: '${DEFAULT_CONTENT}', subject: '${DEFAULT_SUBJECT}', to:  props.RECEPIENT_MAIL_ID

}

def failureEmail(err) {
	emailext( 
			subject: '${JOB_NAME} - BUILD # ${BUILD_NUMBER} -  FAILURE', 
		         body: "${err}",
		        to: props.RECEPIENT_MAIL_ID
		);
	print 'mail sent'
}
return this
