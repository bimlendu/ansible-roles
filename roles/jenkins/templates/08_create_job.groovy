#!groovy
import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*
import hudson.security.*
import hudson.tools.*
import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import hudson.plugins.git.GitSCM
import java.util.logging.Logger
import hudson.plugins.git.*;

def logger = Logger.getLogger("")
def jenkins = Jenkins.getInstance()

def caddy_pipeline_job = "{{ caddy_pipeline_job }}"
def caddy_pipeline_repo = "{{ caddy_pipeline_repo }}"
def caddy_pipeline_file = "{{ caddy_pipeline_file }}"

logger.info('Creating pipeline job: ' + caddy_pipeline_job)

def caddy_pipeline_exists = false
Jenkins.instance.getAllItems().each { j ->
	if (j.fullName == caddy_pipeline_job) {
    	caddy_pipeline_exists = true
    	logger.info('Found existing job: ' + caddy_pipeline_job)
  	}
}

def userRemoteConfig = new UserRemoteConfig(caddy_pipeline_repo, null, null, null)

def scm = new GitSCM(
    Collections.singletonList(userRemoteConfig),
    Collections.singletonList(new BranchSpec("master")),
    false,
    Collections.<SubmoduleConfig>emptyList(),
    null,
    null,
    null)

if (!caddy_pipeline_exists) {
  	WorkflowJob job = Jenkins.instance.createProject(WorkflowJob, caddy_pipeline_job)
  	def definition = new CpsScmFlowDefinition(scm,caddy_pipeline_file)
  	definition.lightweight = true
  	job.definition = definition
}

jenkins.save()