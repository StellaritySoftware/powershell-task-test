package pages

/**
 * Created by Kateryna on 04.11.2017.
 */
class Config {

    static def testFiles = getClass()
            .getResource('/testFiles')
            .toURI()

}