import commonpages.LoginPage
import configuration.CommonConfig
import geb.spock.GebReportingSpec
import pages.PowerShellTaskConfigurationPage
import pages.TaskTypesPage

class SetEnvironmentVariable extends GebReportingSpec
{

    def passEnvironmentVaribleToInlineScript()
    {
        when:

        def loginPage = browser.to LoginPage

        def dashboardPage = loginPage.login(CommonConfig.user, CommonConfig.password)

        def createNewPlanConfigurePlanPage = dashboardPage.createNewPlan()
        createNewPlanConfigurePlanPage.setRandomProjectPlanNames()

        createNewPlanConfigurePlanPage.setNoneRepository()

        def configureTasksPage = createNewPlanConfigurePlanPage.clickConfigurePlanButton()

        def tasks = configureTasksPage.addTask(TaskTypesPage)

        PowerShellTaskConfigurationPage powerShellTaskConfiguration = tasks.selectPowerShellTesttask()
        powerShellTaskConfiguration.scriptLocation = "Inline"
        powerShellTaskConfiguration.inlineScriptBody = "echo \$Env:KEY"
        powerShellTaskConfiguration.environmenVariables = "KEY=VALUE"

        powerShellTaskConfiguration.clickSave()

        configureTasksPage.markEnablePlanCheckbox()

        def createdPlan = configureTasksPage.clickCreateButton()

        def planBuild = createdPlan.runManualBuild()

        then:
        planBuild.waitForSuccessfulHeader()

        when:
        def logsSubPage = planBuild.openLogsSubPage()

        then:
        logsSubPage.logsOutput.text() == "VALUE"
    }
}