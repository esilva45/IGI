package br.com.asyg.connector.bmg.fasap.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import br.com.asyg.connector.bmg.fasap.PreFeedSAP;
import br.com.asyg.connector.bmg.fasap.Util;
import br.com.asyg.connector.bmg.fasap.dao.PreFeedDAO;
import br.com.asyg.connector.bmg.fasap.model.DadosConexaoVO;
import br.com.asyg.connector.bmg.fasap.model.EmpJobVO;

public class EmpJobs {
	private static Logger logger = Logger.getLogger("FASAPConnector");
	
	public void preFeed(DadosConexaoVO dadosConexaoVO, List<String> matriculasAtivas) throws Exception {
		try {
			PreFeedSAP controller = new PreFeedSAP(dadosConexaoVO);
			PreFeedDAO SAPDao = new PreFeedDAO(dadosConexaoVO);
			
			HashMap<String, EmpJobVO> empJobsMap = new HashMap<>();
			List<EmpJobVO> insertEmpJobs = new ArrayList<>();
			List<EmpJobVO> updateEmpJobs = new ArrayList<>();
			List<EmpJobVO> empJobsFA = new ArrayList<>();
			
			Date today = Calendar.getInstance().getTime();
			dadosConexaoVO.setTokenSAP(controller.gerarToken());
			
			List<EmpJobVO> empJobsFAtmp = controller.listEmpJob();
            List<EmpJobVO> empJobsIGI = SAPDao.empJobsDAO();
            
            //Remove as matrículas que não fazem parte do processo de sync
            for (EmpJobVO empJob : empJobsFAtmp) {
                try {
                    if (matriculasAtivas.contains(empJob.getMatricula())) {
                    	if ("S".equalsIgnoreCase(dadosConexaoVO.getAllowMsg())) {
                    		logger.debug("[EmpJobs] CPF " + empJob.getCPF());
                    	}
                    	
                        empJobsFA.add(empJob);
                    }
                } catch (Exception e) {
                    if ("S".equalsIgnoreCase(dadosConexaoVO.getAllowMsg())) {
                    	logger.debug("[EmpJobs] Campo matricula null para CPF " + empJob.getCPF());
                    }
                }
            }
            
            for (EmpJobVO empJob : empJobsIGI) {
                empJobsMap.put(empJob.getMatricula(), empJob);
            }
            
            for (EmpJobVO empJobTarget : empJobsFA) {
                if ("S".equals(dadosConexaoVO.getAllowMsg())) {
                	logger.debug("[EmpJobs] Recon EmpJob: " + empJobTarget.getMatricula());
                }
                
                if (empJobsMap.get(empJobTarget.getMatricula()) == null) {
                    //Se não existe no mapeamento do IGI, será inserido na tabela de pre-feed
                    if ("S".equals(dadosConexaoVO.getAllowMsg())) {
                    	logger.debug("[EmpJobs] Insert: " + empJobTarget.getMatricula());
                    }
                    
                    //Seta valor zero para que a FA depois possa identificar os usuários que precisam ser processados
                    empJobTarget.setIntegrou(0);
                    insertEmpJobs.add(empJobTarget);
                } else {
                    //Caso já exista, verificará se é necessário realizar alguma alteração
                    if ("S".equals(dadosConexaoVO.getAllowMsg())) {
                    	logger.debug("[EmpJobs] Update: " + empJobTarget.getMatricula());
                    }
                    
                    EmpJobVO usuarioAlterar = empJobsMap.get(empJobTarget.getMatricula());
                    
                    empJobTarget = checkDiffEmpJob(empJobTarget, usuarioAlterar);
                    
                    if (empJobTarget.getIntegrou() == 0) {
                        Date dtIniAttrOrg = Util.formatDate(empJobTarget.getDataInicioAtriOrg());
                        
                        if (dtIniAttrOrg.after(today)) {
                            empJobTarget.setEmpresa(usuarioAlterar.getEmpresa());
                            empJobTarget.setGrupoEmpregado(usuarioAlterar.getGrupoEmpregado());
                            empJobTarget.setSubGrupoEmpregado(usuarioAlterar.getSubGrupoEmpregado());
                            empJobTarget.setCentroCusto(usuarioAlterar.getCentroCusto());
                            empJobTarget.setEstabelecimento(usuarioAlterar.getEstabelecimento());
                            empJobTarget.setCargo(usuarioAlterar.getCargo());
                            empJobTarget.setDepartamento(usuarioAlterar.getDepartamento());
                            empJobTarget.setMatriculaGestor(usuarioAlterar.getMatriculaGestor());
                            empJobTarget.setCargoDesc(usuarioAlterar.getCargoDesc());
                            empJobTarget.setEstabelecimentoDesc(usuarioAlterar.getEstabelecimentoDesc());
                            empJobTarget.setEmpresaDesc(usuarioAlterar.getEmpresaDesc());
                            empJobTarget.setDepartamentoDesc(usuarioAlterar.getDepartamentoDesc());
                            empJobTarget.setGrupoEmpregadoDesc(usuarioAlterar.getGrupoEmpregadoDesc());
                            empJobTarget.setSubGrupoEmpregadoDesc(usuarioAlterar.getSubGrupoEmpregadoDesc());
                            empJobTarget.setCentroCustoDesc(usuarioAlterar.getCentroCustoDesc());
                        }
                        
                        empJobTarget.setId(usuarioAlterar.getId());
                        updateEmpJobs.add(empJobTarget);
                    }
                }
            }
	           
            logger.debug("[EmpJobs] Total insertEmpJobs: " + insertEmpJobs.size());
            logger.debug("[EmpJobs] Total updateEmpJobs: " + updateEmpJobs.size());
            
            if (insertEmpJobs.size() > 0) {
				SAPDao.insertEmpJob(insertEmpJobs);
			}
            
            if (updateEmpJobs.size() > 0) {
                SAPDao.updateEmpJob(updateEmpJobs);
            }
		}
		catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e), e);
			throw new Exception("[EmpJobs] Exception preFeed: " + e.getMessage());
	    }
	}
	
	public EmpJobVO checkDiffEmpJob(EmpJobVO empJobTarget, EmpJobVO empJobISVG) throws Exception {
		try {
			empJobTarget.setIntegrou(1);
			
			if (checkDiffValues(empJobTarget.getCargo(), empJobISVG.getCargo())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getCargoDesc(), empJobISVG.getCargoDesc())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getCentroCusto(), empJobISVG.getCentroCusto())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getCentroCustoDesc(), empJobISVG.getCentroCustoDesc())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getDataEmissaoDoc(), empJobISVG.getDataEmissaoDoc())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getDataFim(), empJobISVG.getDataFim())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getDataFimAtriOrg(), empJobISVG.getDataFimAtriOrg())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getDataInicio(), empJobISVG.getDataInicio())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getDataInicioAtriOrg(), empJobISVG.getDataInicioAtriOrg())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getDataNascimento(), empJobISVG.getDataNascimento())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getDataValidadeDoc(), empJobISVG.getDataValidadeDoc())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getDepartamento(), empJobISVG.getDepartamento())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getDepartamentoDesc(), empJobISVG.getDepartamentoDesc())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getEmpresa(), empJobISVG.getEmpresa())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getEmpresaDesc(), empJobISVG.getEmpresaDesc())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getEstabelecimento(), empJobISVG.getEstabelecimento())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getEstabelecimentoDesc(), empJobISVG.getEstabelecimentoDesc())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getGrupoEmpregado(), empJobISVG.getGrupoEmpregado())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getGrupoEmpregadoDesc(), empJobISVG.getGrupoEmpregadoDesc())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getLogin(), empJobISVG.getLogin())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getMatriculaGestor(), empJobISVG.getMatriculaGestor())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getMotivoEvento(), empJobISVG.getMotivoEvento())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getMotivoEventoDesc(), empJobISVG.getMotivoEventoDesc())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getNumRG(), empJobISVG.getNumRG())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getStatusColaborador(), empJobISVG.getStatusColaborador())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getStatusColaboradorDesc(), empJobISVG.getStatusColaboradorDesc())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getSubGrupoEmpregado(), empJobISVG.getSubGrupoEmpregado())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getSubGrupoEmpregadoDesc(), empJobISVG.getSubGrupoEmpregadoDesc())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getUltModifEm(), empJobISVG.getUltModifEm())) {
				empJobTarget.setIntegrou(0);
			} else if (checkDiffValues(empJobTarget.getUltModifEmAtriOrg(), empJobISVG.getUltModifEmAtriOrg())) {
				empJobTarget.setIntegrou(0);
			}
			
			return empJobTarget;
		}
		catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e), e);
			throw new Exception("[EmpJobs] checkDiffEmpJob: " + e.getMessage());
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
			throw new Exception("[EmpJobs] checkDiffValues: " + e.getMessage());
		}
	}
}
