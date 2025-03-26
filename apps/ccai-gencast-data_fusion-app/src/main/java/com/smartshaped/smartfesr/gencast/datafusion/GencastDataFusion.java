package com.smartshaped.smartfesr.gencast.datafusion;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.datafusion.DataFusion;
import com.smartshaped.smartfesr.datafusion.request.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class GencastDataFusion extends DataFusion {
    private static final Logger logger = LogManager.getLogger(GencastDataFusion.class);

    public GencastDataFusion() throws ConfigurationException {
        super();
    }

    @Override
    protected List<String> extractParams(Request req) {
        List <String> list = new ArrayList<>();
        list.add(req.getContent());
        return list;
    }


}
