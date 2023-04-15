import fr.sos.projetmines.commonutils.config.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ConfigurationTest {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationTest.class.getName());

    private Set<OConfigurationExpectation> generateExpectations() {
        Set<OConfigurationExpectation> expectations = new HashSet<>();
        expectations.add(new OStringExpectation("myString", 2, 4));
        expectations.add(new OStringExpectation("mySecondString", 0, 0));
        expectations.add(new OConcatenatedStringExpectation(2, 4, "myString", "mySecondString"));
        expectations.add(new OIntExpectation("myInt", 0, 20));
        expectations.add(new OIntExpectation("mySecondInt", -20, 0));
        expectations.add(new OIntExpectation("myThirdInt", Integer.MIN_VALUE, Integer.MAX_VALUE));
        expectations.add(new ODoubleExpectation("myDouble", 7.2, 27.4));
        return expectations;
    }


    @Test
    public void testCorrect() {
        LOGGER.info("---- TEST testCorrect ----");
        Path configPath = Path.of(System.getProperty("user.dir"), "configuration-correct.properties");
        String defaultConfigPath = "default-configuration.properties";
        AtomicReference<OConfiguration> config = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() ->
                config.set(new OConfiguration(configPath, defaultConfigPath, generateExpectations())));
        try {
            Assertions.assertTrue(config.get().validateConfiguration());
        } catch (IOException exception) {
            Assertions.fail("Validate configurations threw an IOException!", exception.getCause());
        }
        LOGGER.info("-- TEST testCorrect end --");
    }

    @Test
    public void testWrongPath() {
        LOGGER.info("---- TEST testWrongPath ----");
        String defaultConfigPath = "default-configuration.properties";
        Assertions.assertThrows(ConfigurationFilePathException.class,
                () -> new OConfiguration(null, defaultConfigPath, generateExpectations()));
        LOGGER.info("-- TEST testWrongPath end --");
    }


    @Test
    public void testWrongDefaultPath() {
        LOGGER.info("---- TEST testWrongDefaultPath ----");
        Path configPath = Path.of(System.getProperty("user.dir"), "configuration-correct.properties");
        String defaultConfigPath = "";
        Assertions.assertThrows(ConfigurationFilePathException.class,
                () -> new OConfiguration(configPath, defaultConfigPath, generateExpectations()));
        LOGGER.info("-- TEST testWrongDefaultPath end --");
    }

    @Test
    public void testNonExisting() throws IOException {
        LOGGER.info("---- TEST testNonExisting ----");
        Path configPath = Path.of(System.getProperty("user.dir"), "non-existing-config.properties");
        String defaultConfigPath = "default-configuration.properties";
        AtomicReference<OConfiguration> config = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() ->
                config.set(new OConfiguration(configPath, defaultConfigPath, generateExpectations())));
        try {
            Assertions.assertFalse(config.get().validateConfiguration());
        } catch (IOException exception) {
            Assertions.fail("Validate configurations threw an IOException!", exception.getCause());
        }
        Assertions.assertTrue(configPath.toFile().exists());
        if (configPath.toFile().exists()) {
            configPath.toFile().delete();
        }
        LOGGER.info("-- TEST testNonExisting end --");
    }

    @Test
    public void testWrongValues() throws IOException {
        LOGGER.info("---- TEST testWrongValues ----");
        testWrongConfig("configuration-wrong-values.properties");
        LOGGER.info("-- TEST testWrongValues end --");
    }

    @Test
    public void testWrongKeys() throws IOException {
        LOGGER.info("---- TEST testWrongKeys ----");
        testWrongConfig("configuration-wrong-keys.properties");
        LOGGER.info("-- TEST testWrongKeys end --");
    }

    @Test
    public void testWrongOneValue() throws IOException {
        LOGGER.info("---- TEST testWrongOneValue ----");
        testWrongConfig("configuration-wrong-one-value.properties");
        LOGGER.info("-- TEST testWrongOneValue end --");
    }

    @Test
    public void testWrongOneKey() throws IOException {
        LOGGER.info("---- TEST testWrongOneKey ----");
        testWrongConfig("configuration-wrong-one-key.properties");
        LOGGER.info("-- TEST testWrongOneKey end --");
    }

    private void testWrongConfig(String fileName) throws IOException{
        Path configPath = Path.of(System.getProperty("user.dir"), fileName);
        String defaultConfigPath = "default-configuration.properties";
        AtomicReference<OConfiguration> config = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() ->
                config.set(new OConfiguration(configPath, defaultConfigPath, generateExpectations())));
        try {
            Assertions.assertFalse(config.get().validateConfiguration());
        } catch (IOException exception) {
            Assertions.fail("Validate configurations threw an IOException!", exception.getCause());
        }
        File backup = new File(configPath.getFileName().toString().replace(".p", "-backup-0.p"));
        Assertions.assertTrue(backup.exists());
        Files.copy(backup.toPath(), configPath, StandardCopyOption.REPLACE_EXISTING);
        if (backup.exists()) {
            backup.delete();
        }
    }
}
