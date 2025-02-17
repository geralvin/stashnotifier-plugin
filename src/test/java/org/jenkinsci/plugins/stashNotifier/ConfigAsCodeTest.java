package org.jenkinsci.plugins.stashNotifier;

import io.jenkins.plugins.casc.ConfigurationAsCode;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ConfigAsCodeTest {
    @Rule
    public JenkinsRule rule = new JenkinsRule();

    @Test
    public void should_support_jcasc_from_yaml() throws Exception {
        URL configFileUrl = ConfigAsCodeTest.class.getResource(getClass().getSimpleName() + "/configuration-as-code.yml");
        ConfigurationAsCode.get().configure(configFileUrl.toString());
        StashNotifier.DescriptorImpl stashNotifierConfig = rule.jenkins.getDescriptorByType(StashNotifier.DescriptorImpl.class);

        assertThat(stashNotifierConfig.isConsiderUnstableAsSuccess(), equalTo(true));
        assertThat(stashNotifierConfig.getCredentialsId(), equalTo("bitbucket-credentials"));
        assertThat(stashNotifierConfig.isDisableInprogressNotification(), equalTo(true));
        assertThat(stashNotifierConfig.isIgnoreUnverifiedSsl(), equalTo(true));
        assertThat(stashNotifierConfig.isIncludeBuildNumberInKey(), equalTo(true));
        assertThat(stashNotifierConfig.isPrependParentProjectKey(), equalTo(true));
        assertThat(stashNotifierConfig.getStashRootUrl(), equalTo("https://my.company.intranet/bitbucket"));
    }

    @Test
    public void should_support_jcasc_to_yaml() throws Exception {
        StashNotifier.DescriptorImpl stashNotifierConfig = rule.jenkins.getDescriptorByType(StashNotifier.DescriptorImpl.class);

        stashNotifierConfig.setConsiderUnstableAsSuccess(true);
        stashNotifierConfig.setCredentialsId("bitbucket-credentials");
        stashNotifierConfig.setDisableInprogressNotification(true);
        stashNotifierConfig.setIgnoreUnverifiedSsl(true);
        stashNotifierConfig.setIncludeBuildNumberInKey(true);
        stashNotifierConfig.setPrependParentProjectKey(true);
        stashNotifierConfig.setStashRootUrl("https://my.company.intranet/bitbucket");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ConfigurationAsCode.get().export(outputStream);
        String exportedYaml = outputStream.toString("UTF-8");

        InputStream yamlStream = getClass().getResourceAsStream(getClass().getSimpleName() + "/configuration-as-code.yml");
        String expectedYaml = IOUtils.toString(yamlStream, "UTF-8")
                .replaceAll("\r\n?", "\n")
                .replace("unclassified:\n", "");

        assertThat(exportedYaml, containsString(expectedYaml));
    }
}
