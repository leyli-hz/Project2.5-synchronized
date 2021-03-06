package controller;

import exceptions.NotFoundObjException;
import org.apache.log4j.Logger;
import validation.Validations;
import view.PardakhtVO;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Pardakht {
    private Path path = Paths.get("Files/pardakht.txt");
    private final Logger logger = Logger.getLogger(Pardakht.class);

    public Path getPath() {
        return path;
    }

    public Pardakht setPath(Path path) {
        this.path = path;
        return this;
    }

    public List<PardakhtVO> exportToPardakhtVO() {
        logger.debug("run Pardakht.export method");
        List<PardakhtVO> pardakhtList = new ArrayList<>();
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(getPath());
            String line = bufferedReader.readLine();
            while (line != null) {
                PardakhtVO pardakhtVO = new PardakhtVO();
                pardakhtVO.setActionType(view.PardakhtVO.ActionType.findIgnoreCase(line.substring(0, line.indexOf("\t"))));
                pardakhtVO.setAmount(new BigDecimal(line.substring(line.lastIndexOf("\t") + 1, line.length())));
                pardakhtVO.setDepositeNumber(line.substring(line.indexOf("\t") + 1, line.lastIndexOf("\t")));
                logger.debug("pardakhtVO object = " + pardakhtVO.toString());
                pardakhtList.add(pardakhtVO);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            logger.error("something went wrong ! please check if the Mojoodi.txt is available or not !'\r\n'" + e.getMessage());
        } catch (NotFoundObjException e) {
            logger.error(e.getMessage(), e.getCause());
        }
        return pardakhtList;
    }

    public BigDecimal sumOfCreditorMount(List<PardakhtVO> pardakhtVOlist) throws NotFoundObjException {
        logger.debug("run Pardakht.sum method");
        Validations validations = new Validations();
        BigDecimal sum = BigDecimal.ZERO;
        for (PardakhtVO pardakhtVO : pardakhtVOlist) {
            if (validations.isCreditorExist(pardakhtVOlist)) {
                if (pardakhtVO.getActionType().name().equalsIgnoreCase("creditor")) {
                    sum = sum.add(pardakhtVO.getAmount());
                }
            } else {
                throw new NotFoundObjException("There is no Creditor Deposit number please check the pardakht File");
            }
        }
        return sum;
    }


}
