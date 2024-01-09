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
import br.com.asyg.connector.bmg.fasap.model.EmpJobVO;
import br.com.asyg.connector.bmg.fasap.model.PerPersonVO;

public class PerPerson {
	private static Logger logger = Logger.getLogger("FASAPConnector");
	
	public void preFeed(DadosConexaoVO dadosConexaoVO) throws Exception {
		try {
			PreFeedSAP controller = new PreFeedSAP(dadosConexaoVO);
			PreFeedDAO SAPDao = new PreFeedDAO(dadosConexaoVO);
			
			HashMap<String, PerPersonVO> perPersonsMap = new HashMap<>();
			HashMap<String, EmpJobVO> empJobsMap = new HashMap<>();
			List<PerPersonVO> insertPerPerson = new ArrayList<>();
			List<PerPersonVO> updatePerPerson = new ArrayList<>();
			
			dadosConexaoVO.setTokenSAP(controller.gerarToken());
			
			// Mapeia todos registros de PerPerson
			List<PerPersonVO> perPersonFA = controller.listPerPerson();
			List<PerPersonVO> perPersonIGI = SAPDao.perPersonsDAO();
			List<EmpJobVO> empJobsIGI = SAPDao.empJobsDAO();
			
			for (EmpJobVO empJobs : empJobsIGI) {
				empJobsMap.put(empJobs.getMatricula(), empJobs);
			}
			
			for (PerPersonVO perPerson : perPersonIGI) {
				perPersonsMap.put(perPerson.getPersonId(), perPerson);
			}
			
			for (PerPersonVO personTarget : perPersonFA) {
				if ("S".equals(dadosConexaoVO.getAllowMsg())) {
					logger.debug("[PerPerson] Recon PerPerson: " +  personTarget.getPersonId());
				}
				
				if (empJobsMap.get(personTarget.getPersonId()) != null) {
					if (perPersonsMap.get(personTarget.getPersonId()) == null) {
						// Se nao existe no mapeamento do IGI, sera inserido na tabela de pre-feed
						if ("S".equals(dadosConexaoVO.getAllowMsg())) {
							logger.debug("[PerPerson] Insert: " +  personTarget.getPersonId());
						}
						
						// Seta valor zero para que a FA depois possa identificar os usuarios que precisam ser processados
						personTarget.setIntegrou(0);
						insertPerPerson.add(personTarget);
					} else {
						// Caso ja exista, verificara se e necessario realizar alguma alteracao
						if ("S".equals(dadosConexaoVO.getAllowMsg())) {
							logger.debug("[PerPerson] Update: " +  personTarget.getPersonId());
						}
						
						PerPersonVO personAlterar = perPersonsMap.get(personTarget.getPersonId());
						
						personTarget = checkDiffPerson(personTarget, personAlterar);
						
						if (personTarget.getIntegrou() == 0) {
							personTarget.setId(personAlterar.getId());
							updatePerPerson.add(personTarget);
						}
					}
				}
			}

			logger.debug("[PerPerson] Total insertPerPerson: " + insertPerPerson.size());
			logger.debug("[PerPerson] Total updatePerPerson: " + updatePerPerson.size());
			
			if (insertPerPerson.size() > 0) {
				SAPDao.insertPerPerson(insertPerPerson);
			}
			
			if (updatePerPerson.size() > 0) {
				SAPDao.updatePerPerson(updatePerPerson);
			}
		}
		catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e), e);
			throw new Exception("[PerPerson] Exception preFeed: " + e.getMessage());
	    }
	}
	
	public PerPersonVO checkDiffPerson(PerPersonVO personTarget, PerPersonVO personAlterar) throws Exception {
		try {
			personTarget.setIntegrou(1);
			
			if (checkDiffValues(personTarget.getPersonId(), personAlterar.getPersonId())) {
				personTarget.setIntegrou(0);
			} else if (checkDiffValues(personTarget.getAreaCode(), personAlterar.getAreaCode())) {
				personTarget.setIntegrou(0);
			} else if (checkDiffValues(personTarget.getPhoneNumber(), personAlterar.getPhoneNumber())) {
				personTarget.setIntegrou(0);
			}
			
			return personTarget;
		}
		catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e), e);
			throw new Exception("[PerPerson] checkDiffPerson: " + e.getMessage());
		}
	}
	
	public boolean checkDiffValues(String targetValue, String ISVGValue) throws Exception {
		try {
			if (StringUtils.isEmpty(targetValue) && !StringUtils.isEmpty(ISVGValue) || (!StringUtils.isEmpty(targetValue) && !targetValue.equals(ISVGValue))) {
				if (!targetValue.equalsIgnoreCase(ISVGValue)) {
					logger.debug("[PerPerson] PerPerson DiffValues: [" + targetValue + "] [" + ISVGValue + "]");
					return true;
				}
			}
			
			return false;
		}
		catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e), e);
			throw new Exception("[PerPerson] checkDiffValues: " + e.getMessage());
		}
	}
}
