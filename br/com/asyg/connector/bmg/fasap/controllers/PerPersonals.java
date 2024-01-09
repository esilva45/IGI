package br.com.asyg.connector.bmg.fasap.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import br.com.asyg.connector.bmg.fasap.PreFeedSAP;
import br.com.asyg.connector.bmg.fasap.dao.PreFeedDAO;
import br.com.asyg.connector.bmg.fasap.model.DadosConexaoVO;
import br.com.asyg.connector.bmg.fasap.model.PerPersonalVO;

public class PerPersonals {
	private static Logger logger = Logger.getLogger("FASAPConnector");

	public List<String> preFeed(DadosConexaoVO dadosConexaoVO) throws Exception {
		List<String> matriculasAtivas = new ArrayList<>();
		
		try {
			PreFeedSAP controller = new PreFeedSAP(dadosConexaoVO);
			PreFeedDAO SAPDao = new PreFeedDAO(dadosConexaoVO);
			
			HashMap<String, PerPersonalVO> perPersonalsMap = new HashMap<>();
			List<PerPersonalVO> insertPerPersonals = new ArrayList<>();
			List<PerPersonalVO> updatePerPersonals = new ArrayList<>();
			
			dadosConexaoVO.setTokenSAP(controller.gerarToken());
			
            List<PerPersonalVO> perPersonalsFA = controller.listPerPersonal();
            List<PerPersonalVO> perPersonalsIGI = SAPDao.perPersonalsDAO();
            
            for (PerPersonalVO perPersonal : perPersonalsIGI) {
                perPersonalsMap.put(perPersonal.getPersonIdExternal(), perPersonal);
            }
            
            for (PerPersonalVO perPersonal : perPersonalsFA) {
                matriculasAtivas.add(perPersonal.getValidPersonalId());
            }
            
            for (PerPersonalVO perPersonalTarget : perPersonalsFA) {
                if ("S".equals(dadosConexaoVO.getAllowMsg())) {
                	logger.debug("[PerPersonals] Recon PerPersonal: " + perPersonalTarget.getPersonIdExternal());
                }
                
                if (perPersonalsMap.get(perPersonalTarget.getPersonIdExternal()) == null) {
                    //Se não existe no mapeamento do IGI, será inserido na tabela de pre-feed
                    if ("S".equals(dadosConexaoVO.getAllowMsg())) {
                    	logger.debug("[PerPersonals] Insert: " + perPersonalTarget.getPersonIdExternal());
                    }
                    
                    //Seta valor zero para que a FA depois possa identificar os usuários que precisam ser processados
                    perPersonalTarget.setIntegrou(0);
                    insertPerPersonals.add(perPersonalTarget);
                } else {
                    //Caso já exista, verificará se é necessário realizar alguma alteração
                    if ("S".equals(dadosConexaoVO.getAllowMsg())) {
                    	logger.debug("[PerPersonals] Update: " + perPersonalTarget.getPersonIdExternal());
                    }
                    
                    PerPersonalVO usuarioAlterar = perPersonalsMap.get(perPersonalTarget.getPersonIdExternal());

                    perPersonalTarget = checkDiffPerPersonal(perPersonalTarget, usuarioAlterar);
                    
                    if (perPersonalTarget.getIntegrou() == 0) {
                        perPersonalTarget.setId(usuarioAlterar.getId());
                        updatePerPersonals.add(perPersonalTarget);
                    }
                }
            }
            
            logger.debug("[PerPersonals] Total insertPerPersonals: " + insertPerPersonals.size());
            logger.debug("[PerPersonals] Total updatePerPersonals: " + updatePerPersonals.size());
            
            if (insertPerPersonals.size() > 0) {
                SAPDao.insertPerPersonal(insertPerPersonals);
            }
            
            if (updatePerPersonals.size() > 0) {
                SAPDao.updatePerPersonal(updatePerPersonals);
            }
		}
		catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e), e);
			throw new Exception("[PerPersonals] Exception preFeed: " + e.getMessage());
	    }
		
		return matriculasAtivas;
	}
	
	public PerPersonalVO checkDiffPerPersonal(PerPersonalVO perPersonalTarget, PerPersonalVO perPersonalISVG) throws Exception {
		try {
			perPersonalTarget.setIntegrou(1);
			
			if (checkDiffValues(perPersonalTarget.getLastName(), perPersonalISVG.getLastName())) {
				perPersonalTarget.setIntegrou(0);
			} else if (checkDiffValues(perPersonalTarget.getFirstName(), perPersonalISVG.getFirstName())) {
				perPersonalTarget.setIntegrou(0);
			} else if (checkDiffValues(perPersonalTarget.getPreferedName(), perPersonalISVG.getPreferedName())) {
				perPersonalTarget.setIntegrou(0);
			} else if (checkDiffValues(perPersonalTarget.getGender(), perPersonalISVG.getGender())) {
				perPersonalTarget.setIntegrou(0);
			} else if (checkDiffValues(perPersonalTarget.getStartDate(), perPersonalISVG.getStartDate())) {
				perPersonalTarget.setIntegrou(0);
			} else if (checkDiffValues(perPersonalTarget.getEndDate(), perPersonalISVG.getEndDate())) {
				perPersonalTarget.setIntegrou(0);
			} else if (checkDiffValues(perPersonalTarget.getValidPersonalId(), perPersonalISVG.getValidPersonalId())) {
				perPersonalTarget.setIntegrou(0);
			}
			
			return perPersonalTarget;
		}
		catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e), e);
			throw new Exception("[PerPersonals] checkDiffPerPersonal: " + e.getMessage());
		}
	}
	
	public boolean checkDiffValues(String targetValue, String ISVGValue) throws Exception {
		try {
			if (StringUtils.isEmpty(targetValue) && !StringUtils.isEmpty(ISVGValue) || (!StringUtils.isEmpty(targetValue) && !targetValue.equals(ISVGValue))) {
				if (!targetValue.equalsIgnoreCase(ISVGValue)) {
					return true;
				}
			}
			
			return false;
		}
		catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e), e);
			throw new Exception("[PerPersonals] checkDiffValues: " + e.getMessage());
		}
	}
}
