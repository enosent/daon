package daon.manager.config;

import daon.core.DaonAnalyzer;
import daon.core.model.ModelInfo;
import daon.manager.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by mac on 2017. 4. 18..
 */
@Configuration
@ComponentScan(value = "daon.manager")
public class DaonConfig {

    @Autowired
    private ModelService modelService;

    @Bean
    public DaonAnalyzer analyzer() throws IOException {

        ModelInfo modelInfo = modelService.defaultModelInfo();
        return new DaonAnalyzer(modelInfo);
    }


    @Bean(destroyMethod="shutdownNow")
    public ExecutorService executorService(){

        return new ThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(1), new ThreadPoolExecutor.DiscardPolicy());
    }

}
