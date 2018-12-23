import commonpages.LoginPage
import configuration.CommonConfig
import geb.spock.GebReportingSpec
import helpers.DirectoryCreator
import pages.PowerShellTaskConfigurationPage
import pages.TaskTypesPage

class ScriptFromFileTest extends GebReportingSpec
{

    def test_1()
    {
        when:

        def loginPage = browser.to LoginPage

        def dashboardPage = loginPage.login(CommonConfig.user, CommonConfig.password)

        def createNewPlanConfigurePlanPage = dashboardPage.createNewPlan()
        createNewPlanConfigurePlanPage.setRandomProjectPlanNames()

        DirectoryCreator.createPlanDirectory()
        DirectoryCreator.copyFile("output_text.ps1")

        createNewPlanConfigurePlanPage.setNoneRepository()

        def configureTasksPage = createNewPlanConfigurePlanPage.clickConfigurePlanButton()

        def tasks = configureTasksPage.addTask(TaskTypesPage)

        PowerShellTaskConfigurationPage powerShellTaskConfiguration = tasks.selectPowerShellTesttask()
        powerShellTaskConfiguration.taskDescription << "my_task"
        powerShellTaskConfiguration.scriptLocation = "File"
        powerShellTaskConfiguration.scrollTo500PX()
        powerShellTaskConfiguration.scriptFile = "output_text.ps1"

        powerShellTaskConfiguration.clickSave()

        configureTasksPage.markEnablePlanCheckbox()

        def createdPlan = configureTasksPage.clickCreateButton()

        def planBuild = createdPlan.runManualBuild()

        then:
        planBuild.waitForSuccessfulHeader()
    }

    def test_2()
    {
        when:

        def loginPage = browser.to LoginPage

        def dashboardPage = loginPage.login(CommonConfig.user, CommonConfig.password)

        def createNewPlanConfigurePlanPage = dashboardPage.createNewPlan()
        createNewPlanConfigurePlanPage.setRandomProjectPlanNames()

        DirectoryCreator.createPlanDirectory()
        DirectoryCreator.copyFile("exit_code.ps1")

        createNewPlanConfigurePlanPage.setNoneRepository()

        def configureTasksPage = createNewPlanConfigurePlanPage.clickConfigurePlanButton()

        def tasks = configureTasksPage.addTask(TaskTypesPage)

        PowerShellTaskConfigurationPage powerShellTaskConfiguration = tasks.selectPowerShellTesttask()
        powerShellTaskConfiguration.taskDescription << "my_task"
        powerShellTaskConfiguration.scriptLocation = "File"
        powerShellTaskConfiguration.scrollTo500PX()
        powerShellTaskConfiguration.scriptFile = "exit_code.ps1"

        powerShellTaskConfiguration.clickSave()

        configureTasksPage.markEnablePlanCheckbox()

        def createdPlan = configureTasksPage.clickCreateButton()

        def planBuild = createdPlan.runManualBuild()

        then:
        planBuild.waitForFailedHeader()
    }

    def test_3()
    {
        when:

        def loginPage = browser.to LoginPage

        def dashboardPage = loginPage.login(CommonConfig.user, CommonConfig.password)

        def createNewPlanConfigurePlanPage = dashboardPage.createNewPlan()
        createNewPlanConfigurePlanPage.setRandomProjectPlanNames()

        DirectoryCreator.createPlanDirectory()
        DirectoryCreator.copyFile("wrong_command.ps1")

        createNewPlanConfigurePlanPage.setNoneRepository()

        def configureTasksPage = createNewPlanConfigurePlanPage.clickConfigurePlanButton()

        def tasks = configureTasksPage.addTask(TaskTypesPage)

        PowerShellTaskConfigurationPage powerShellTaskConfiguration = tasks.selectPowerShellTesttask()
        powerShellTaskConfiguration.taskDescription << "my_task"
        powerShellTaskConfiguration.scriptLocation = "File"
        powerShellTaskConfiguration.scrollTo500PX()
        powerShellTaskConfiguration.scriptFile = "wrong_command.ps1"

        powerShellTaskConfiguration.clickSave()

        configureTasksPage.markEnablePlanCheckbox()

        def createdPlan = configureTasksPage.clickCreateButton()

        def planBuild = createdPlan.runManualBuild()

        then:
        planBuild.waitForFailedHeader()
    }
}