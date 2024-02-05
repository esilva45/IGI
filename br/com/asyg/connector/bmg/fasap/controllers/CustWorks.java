package br.com.asyg.connector.bmg.fasap.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import br.com.asyg.connector.bmg.fasap.PreFeedSAP;
import br.com.asyg.connector.bmg.fasap.dao.PreFeedDAO;
import br.com.asyg.connector.bmg.fasap.model.CustWorkForceSoftwareVO;
import br.com.asyg.connector.bmg.fasap.model.DadosConexaoVO;

public class CustWorks {
	private static Logger logger = Logger.getLogger("FASAPConnector");

	public void preFeed(DadosConexaoVO dadosConexaoVO) throws Exception {	
		try {
			PreFeedSAP controller = new PreFeedSAP(dadosConexaoVO);
			PreFeedDAO SAPDao = new PreFeedDAO(dadosConexaoVO);
			
			List<CustWorkForceSoftwareVO> insertCustWorks = new ArrayList<>();
			
			dadosConexaoVO.setTokenSAP(controller.gerarToken());
			
            List<CustWorkForceSoftwareVO> custWorkForcesFA = controller.listCustWorkForceSoftware();
            List<CustWorkForceSoftwareVO> custWorkForceSoftwaresIGI = SAPDao.custWorkForceSoftwares();
            
            for (CustWorkForceSoftwareVO custWorkTarget : custWorkForcesFA) {
                if ("S".equals(dadosConexaoVO.getAllowMsg())) {
                	logger.debug("[CustWorks] Recon WorkForceSoftware: " + custWorkTarget.getExternalName());
                }
                
                if (!custWorkForceSoftwaresIGI.contains(custWorkTarget)) {
                    //Se não existe no mapeamento do IGI, será inserido na tabela de pre-feed
                    if ("S".equals(dadosConexaoVO.getAllowMsg())) {
                    	logger.debug("[CustWorks] Insert: " + custWorkTarget.getExternalName());
                    }
                    
                    //Seta valor zero para que a FA depois possa identificar os usuários que precisam ser processados
                    custWorkTarget.setIntegrou(0);
                    insertCustWorks.add(custWorkTarget);
                }
            }

            logger.debug("[CustWorks] Total insertCustWorks: " + insertCustWorks.size());
            
            if (insertCustWorks.size() > 0) {
                SAPDao.insertCustWorkForce(insertCustWorks);
            }
		}
		catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e), e);
			throw new Exception("[CustWorks] Exception preFeed: " + e.getMessage());
	    }
	}
	
	public boolean checkDiffValues(String targetValue, String ISVGValue) throws Exception {
		try {
			if (StringUtils.isEmpty(targetValue) && !StringUtils.isEmpty(ISVGValue) || (!StringUtils.isEmpty(targetValue) && !targetValue.equals(ISVGValue))) {
				if (ISVGValue != targetValue)
					return true;
			}
			
			return false;
		}
		catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e), e);
			throw new Exception("[CustWorks] checkDiffValues: " + e.getMessage());
		}
	}
	
	public CustWorkForceSoftwareVO checkDiffCustWork(CustWorkForceSoftwareVO custWorkTarget, CustWorkForceSoftwareVO custWorkISVG) throws Exception {
		try {
			custWorkTarget.setIntegrou(1);
			
			if (checkDiffValues(custWorkTarget.getDateEnd(), custWorkISVG.getDateEnd())) {
				custWorkTarget.setIntegrou(0);
			} else if (checkDiffValues(custWorkTarget.getDateStart(), custWorkISVG.getDateStart())) {
				custWorkTarget.setIntegrou(0);
			} else if (checkDiffValues(custWorkTarget.getAbsencesDays(), custWorkISVG.getAbsencesDays())) {
				custWorkTarget.setIntegrou(0);
			} else if (checkDiffValues(custWorkTarget.getAbsenceType(), custWorkISVG.getAbsenceType())) {
				custWorkTarget.setIntegrou(0);
			} else if (checkDiffValues(custWorkTarget.getLastModDate(), custWorkISVG.getLastModDate())) {
				custWorkTarget.setIntegrou(0);
			} else if (checkDiffValues(custWorkTarget.getAbsenceTypeDescription(), custWorkISVG.getAbsenceTypeDescription())) {
				custWorkTarget.setIntegrou(0);
			} else if (checkDiffValues(custWorkTarget.getStatus(), custWorkISVG.getStatus())) {
				custWorkTarget.setIntegrou(0);
			}
			
			return custWorkTarget;
		}
		catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e), e);
			throw new Exception("[CustWorks] CustWorkForceSoftwareVO: " + e.getMessage());
		}
	}
}
