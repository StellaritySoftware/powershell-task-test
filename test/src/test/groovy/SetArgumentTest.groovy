import commonpages.LoginPage
import configuration.CommonConfig
import geb.spock.GebReportingSpec
import helpers.DirectoryCreator
import pages.PowerShellTaskConfigurationPage
import pages.TaskTypesPage

class SetArgumentTest extends GebReportingSpec
{

    def passArgumentToInlineScript()
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
        powerShellTaskConfiguration.inlineScriptBody = "echo \$Args[0]"
        powerShellTaskConfiguration.arguments = "TestArgument"

        powerShellTaskConfiguration.clickSave()

        configureTasksPage.markEnablePlanCheckbox()

        def createdPlan = configureTasksPage.clickCreateButton()

        def planBuild = createdPlan.runManualBuild()

        then:
        planBuild.waitForSuccessfulHeader()

        when:
        def logsSubPage = planBuild.openLogsSubPage()

        then:
        logsSubPage.logsOutput.text() == "TestArgument"
    }

    def passArgumentToFileScript()
    {
        when:

        def loginPage = browser.to LoginPage

        def dashboardPage = loginPage.login(CommonConfig.user, CommonConfig.password)

        def createNewPlanConfigurePlanPage = dashboardPage.createNewPlan()
        createNewPlanConfigurePlanPage.setRandomProjectPlanNames()

        DirectoryCreator.createPlanDirectory()
        DirectoryCreator.copyFile("echoArg.ps1")

        createNewPlanConfigurePlanPage.setNoneRepository()

        def configureTasksPage = createNewPlanConfigurePlanPage.clickConfigurePlanButton()

        def tasks = configureTasksPage.addTask(TaskTypesPage)

        PowerShellTaskConfigurationPage powerShellTaskConfiguration = tasks.selectPowerShellTesttask()
        powerShellTaskConfiguration.scriptLocation = "File"
        powerShellTaskConfiguration.scrollTo500PX()
        powerShellTaskConfiguration.scriptFile = "echoArg.ps1"
        powerShellTaskConfiguration.arguments = "TestArgument"

        powerShellTaskConfiguration.clickSave()

        configureTasksPage.markEnablePlanCheckbox()

        def createdPlan = configureTasksPage.clickCreateButton()

        def planBuild = createdPlan.runManualBuild()

        then:
        planBuild.waitForSuccessfulHeader()

        when:
        def logsSubPage = planBuild.openLogsSubPage()

        then:
        logsSubPage.logsOutput.text() == "TestArgument"
    }

}