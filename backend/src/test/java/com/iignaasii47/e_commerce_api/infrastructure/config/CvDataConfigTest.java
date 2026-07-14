package com.iignaasii47.e_commerce_api.infrastructure.config;

import com.iignaasii47.e_commerce_api.domain.port.out.CvDataProvider;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;

class CvDataConfigTest {

    @Test
    void shouldLoadCvContentWhenResourceExists() {
        CvDataConfig config = new CvDataConfig();
        ResourceLoader loader = new TestResourceLoader(new ByteArrayResource("CV data content".getBytes()));

        CvDataProvider provider = config.cvDataProvider(loader);

        assertThat(provider.getCvContent()).isEqualTo("CV data content");
    }

    @Test
    void shouldReturnFallbackWhenResourceDoesNotExist() {
        CvDataConfig config = new CvDataConfig();
        Resource resource = new ByteArrayResource(new byte[0]) {
            @Override
            public boolean exists() {
                return false;
            }
        };
        ResourceLoader loader = new TestResourceLoader(resource);

        CvDataProvider provider = config.cvDataProvider(loader);

        assertThat(provider.getCvContent()).contains("No CV data available");
    }

    @Test
    void shouldReturnFallbackWhenIOExceptionOccurs() {
        CvDataConfig config = new CvDataConfig();
        Resource resource = new ByteArrayResource(new byte[0]) {
            @Override
            public boolean exists() {
                return true;
            }

            @Override
            public String getContentAsString(java.nio.charset.Charset charset) throws java.io.IOException {
                throw new java.io.IOException("read error");
            }
        };
        ResourceLoader loader = new TestResourceLoader(resource);

        CvDataProvider provider = config.cvDataProvider(loader);

        assertThat(provider.getCvContent()).contains("Failed to load CV data");
    }

    private static class TestResourceLoader implements ResourceLoader {

        private final Resource resource;

        TestResourceLoader(Resource resource) {
            this.resource = resource;
        }

        @Override
        public Resource getResource(String location) {
            return resource;
        }

        @Override
        public ClassLoader getClassLoader() {
            return getClass().getClassLoader();
        }

    }

}
