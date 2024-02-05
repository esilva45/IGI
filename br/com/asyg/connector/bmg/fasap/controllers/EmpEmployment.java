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
import br.com.asyg.connector.bmg.fasap.model.EmpEmploymentVO;
import br.com.asyg.connector.bmg.fasap.model.EmpJobVO;

public class EmpEmployment {
	private static Logger logger = Logger.getLogger("FASAPConnector");

	public void preFeed(DadosConexaoVO dadosConexaoVO) throws Exception {
		try {
			PreFeedSAP controller = new PreFeedSAP(dadosConexaoVO);
			PreFeedDAO SAPDao = new PreFeedDAO(dadosConexaoVO);
			
			HashMap<String, EmpEmploymentVO> empEmploymentsMap = new HashMap<>();
			HashMap<String, EmpJobVO> empJobsMap = new HashMap<>();
			List<EmpEmploymentVO> insertEmpEmployment = new ArrayList<>();
			List<EmpEmploymentVO> updateEmpEmployment = new ArrayList<>();
			
			dadosConexaoVO.setTokenSAP(controller.gerarToken());
			
			List<EmpEmploymentVO> empEmploymentFA = controller.listEmpEmployment();
			List<EmpEmploymentVO> empEmploymentIGI = SAPDao.empEmploymentDAO();
			List<EmpJobVO> empJobsIGI = SAPDao.empJobsDAO();
			
			for (EmpJobVO empJobs : empJobsIGI) {
				empJobsMap.put(empJobs.getMatricula(), empJobs);
			}
			
			for (EmpEmploymentVO empEmployment : empEmploymentIGI) {
				empEmploymentsMap.put(empEmployment.getPersonId(), empEmployment);
			}
			
			for (EmpEmploymentVO empEmploymentTarget : empEmploymentFA) {
				if ("S".equals(dadosConexaoVO.getAllowMsg())) {
					logger.debug("[EmpEmployment] Recon EmpEmployment: " +  empEmploymentTarget.getPersonId());
				}
				
				if (empJobsMap.get(empEmploymentTarget.getPersonId()) != null) {
					if (empEmploymentsMap.get(empEmploymentTarget.getPersonId()) == null) {
						// Se nao existe no mapeamento do IGI, sera inserido na tabela de pre-feed
						if ("S".equals(dadosConexaoVO.getAllowMsg())) {
							logger.debug("[EmpEmployment] Insert: " +  empEmploymentTarget.getPersonId());
						}
						
						// Seta valor zero para que a FA depois possa identificar os usuarios que precisam ser processados
						empEmploymentTarget.setIntegrou(0);
						insertEmpEmployment.add(empEmploymentTarget);
					} else {
						// Caso ja exista, verificara se e necessario realizar alguma alteracao
						if ("S".equals(dadosConexaoVO.getAllowMsg())) {
							logger.debug("[EmpEmployment] Update: " +  empEmploymentTarget.getPersonId());
						}
						
						EmpEmploymentVO empEmploymentAlterar = empEmploymentsMap.get(empEmploymentTarget.getPersonId());
						
						empEmploymentTarget = checkDiffEmpEmployment(empEmploymentTarget, empEmploymentAlterar);
						
						if (empEmploymentTarget.getIntegrou() == 0) {
							empEmploymentTarget.setId(empEmploymentAlterar.getId());
							updateEmpEmployment.add(empEmploymentTarget);
						}
					}
				}
			}
			
			logger.debug("[EmpEmployment] Total insertEmpEmployment: " + insertEmpEmployment.size());
			logger.debug("[EmpEmployment] Total updateEmpEmployment: " + updateEmpEmployment.size());
			
			if (insertEmpEmployment.size() > 0) {
				SAPDao.insertEmpEmployment(insertEmpEmployment);
			}
			
			if (updateEmpEmployment.size() > 0) {
				SAPDao.updateEmpEmployment(updateEmpEmployment);
			}
		}
		catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e), e);
			throw new Exception("[EmpEmployment] Exception preFeed: " + e.getMessage());
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
			throw new Exception("[EmpEmployment] checkDiffValues: " + e.getMessage());
		}
	}
	
	public EmpEmploymentVO checkDiffEmpEmployment(EmpEmploymentVO empEmploymentTarget, EmpEmploymentVO empEmploymentAlterar) throws Exception {
		try {
			empEmploymentTarget.setIntegrou(1);
			
			if (checkDiffValues(empEmploymentTarget.getPersonId(), empEmploymentAlterar.getPersonId())) {
				empEmploymentTarget.setIntegrou(0);
			} else if (checkDiffValues(empEmploymentTarget.getNivelCargo(), empEmploymentAlterar.getNivelCargo())) {
				empEmploymentTarget.setIntegrou(0);
			}
			
			return empEmploymentTarget;
		}
		catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e), e);
			throw new Exception("[EmpEmployment] checkDiffEmpEmployment: " + e.getMessage());
		}
	}
}
