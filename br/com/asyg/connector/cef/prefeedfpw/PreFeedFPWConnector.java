package br.com.asyg.connector.cef.prefeedfpw;

import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import com.ibm.di.connector.Connector;
import com.ibm.di.connector.ConnectorInterface;
import com.ibm.di.entry.Entry;
import com.ibm.di.server.SearchCriteria;

import br.com.asyg.connector.cef.prefeedfpw.model.User;
import br.com.asyg.connector.exceptions.ConnectorException;

public class PreFeedFPWConnector extends Connector implements ConnectorInterface {
	public static final String[] CONNECTOR_MODES = new String[] { "Iterator", "Lookup" };
	private static final String COPYRIGHT = "Licensed Materials - Property of Asyg";
	private static final String myName = "PreFeedFPW Connector";
	private static String VERSION_INFO = null;
	private String fpwpassword, fpwurl, fpwuser, fpwtable, fwpinstancia, igipassword, igiurl, igiuser, events, prefeedtable, mattable, pesotable;

	public PreFeedFPWConnector() {
		setName(myName);
		setModes(CONNECTOR_MODES);
	}

	public void terminate() throws Exception {
		this.fpwurl = null;
		this.fpwuser = null;
		this.fpwpassword = null;
		this.fpwtable = null;
		this.fwpinstancia = null;
		this.igiurl = null;
		this.igiuser = null;
		this.igipassword = null;
		this.prefeedtable = null;
		this.mattable = null;
		this.pesotable = null;
		this.events = null;
		super.terminate();
	}

	public void initialize(Object o) throws Exception {
		logmsg("Initialize HRFeedConnector");
		this.fpwurl = getParam("fpwurl");
		this.fpwuser = getParam("fpwuser");
		this.fpwpassword = getParam("fpwpassword");
		this.fpwtable = getParam("fpwtable");
		this.fwpinstancia = getParam("fwpinstancia");
		this.igiurl = getParam("igiurl");
		this.igiuser = getParam("igiuser");
		this.igipassword = getParam("igipassword");
		this.prefeedtable = getParam("prefeedtable");
		this.mattable = getParam("mattable");
		this.pesotable = getParam("pesotable");
		this.events = getParam("events");
	}

	public void selectEntries() throws Exception {
		Connection conn_fpw = null;
		Connection conn_igi = null;

		try {
			conn_fpw = Utils.getConnection("net.sourceforge.jtds.jdbc.Driver", fpwurl, fpwuser, fpwpassword);
			conn_igi = Utils.getConnection("oracle.jdbc.driver.OracleDriver", igiurl, igiuser, igipassword);

			OracleDAO preFeedDAO = new OracleDAO(conn_igi);
			List<String> enrollmentPreFeed = preFeedDAO.listEnrollmentPersonPreFeed(mattable);

			FpwDAO fpwDao = new FpwDAO(conn_fpw);
			List<String> personFPW = fpwDao.listFuPersonIDs(fwpinstancia, fpwtable);

			for (String pessoaIDNoFPW : personFPW) {
				if (!enrollmentPreFeed.contains(pessoaIDNoFPW)) {
					logmsg("Inserindo Matricula: " + pessoaIDNoFPW);
					preFeedDAO.insertPersonId(mattable, pessoaIDNoFPW);
				}
			}

			// Pessoas ja gravadas no Pre-Feed
			List<User> pessoasNoPreFeed = preFeedDAO.listPersonPreFeed(prefeedtable);
			Map<String, User> mapaPessoas = new HashMap<String, User>();

			// Montando o Map para comparar se ouve diferença
			for (User pessoaVO : pessoasNoPreFeed) {
				mapaPessoas.put(pessoaVO.getFUPessoaID(), pessoaVO);
			}

			// 1a validacao Férias - Afastamento
			String QUERY_FERIAS = " SELECT  FUMatFunc, FUNomFunc, FUSexFunc, FUDtNasc, FUCodSitu, FUDtIniSit, FUDtAdmis, FUCodLot, FUCodCargo, FUIndBateP, FUIndIRRF, FUCPF, FUOutDcTip, FUOutDcNum,FUCentrCus, "
					+ "FUEmail, FUCelular, FUCodGrpHie, FUEmail2, FUTipoNecEsp, FUPessoaID, FUCODEMP, GHCODGRPHIE, GHNUMERONIVELHIERARQUICO, GHDESC, GHCODGRPHIERARQUICOSUPERIOR,CADESCARGO, "
					+ "CARGCODHIE, LODESCLOT, AEVALOR, GHDESCRESUM, OFMatFunc, OFDtIniOco, OFDtFinOco, OFNumDiaAx, OFNumDiaFe, OFCODOCORR, OPNOMOPER, DTH_ALTERACAO_FUNCIONARIO,GESTOR, MAT_GESTOR, HIERDESHIE, HIERCODHIE "
					+ "FROM " + fwpinstancia + "." + fpwtable
					+ " where OFDtIniOco between FORMAT(getdate() -10 , 'yyyyMMdd') and FORMAT(getdate()+1, 'yyyyMMdd') and  fupessoaid = ?  and OFCODOCORR not in ("
					+ events + ") " + " union "
					+ "SELECT  FUMatFunc, FUNomFunc, FUSexFunc, FUDtNasc, FUCodSitu, FUDtIniSit, FUDtAdmis, FUCodLot, FUCodCargo, FUIndBateP, FUIndIRRF, FUCPF, FUOutDcTip, FUOutDcNum,FUCentrCus, "
					+ "FUEmail, FUCelular, FUCodGrpHie, FUEmail2, FUTipoNecEsp, FUPessoaID, FUCODEMP, GHCODGRPHIE, GHNUMERONIVELHIERARQUICO, GHDESC, GHCODGRPHIERARQUICOSUPERIOR,CADESCARGO, "
					+ "CARGCODHIE, LODESCLOT, AEVALOR, GHDESCRESUM, OFMatFunc, OFDtIniOco, OFDtFinOco, OFNumDiaAx, OFNumDiaFe, OFCODOCORR, OPNOMOPER, DTH_ALTERACAO_FUNCIONARIO,GESTOR, MAT_GESTOR, HIERDESHIE, HIERCODHIE "
					+ "FROM " + fwpinstancia + "." + fpwtable
					+ " where OFDtFinOco between FORMAT(getdate() -10 , 'yyyyMMdd') and FORMAT(getdate()+1, 'yyyyMMdd')  and fupessoaid = ?   and OFCODOCORR not in ("
					+ events + ") ";

			for (String pessoaIDNoFPW : personFPW) {
				List<User> listaFerias = fpwDao.checksPersonFPW(pessoaIDNoFPW, QUERY_FERIAS, Boolean.TRUE, conn_fpw, null);

				User usuarioFerias = null;

				if (listaFerias.size() > 1) {
					boolean temDesligamento = Utils.temDesligamento(listaFerias);

					if (temDesligamento) {
						// valida qual ocorrencia maior
						usuarioFerias = Utils.ocorrenciaValida(pesotable,listaFerias, preFeedDAO);
					} else {
						// não tem nenhuma outra validação hoje
						usuarioFerias = listaFerias.get(0);
					}

				} else {
					if (listaFerias.size() > 0) {
						usuarioFerias = listaFerias.get(0);
					}
				}

				if (usuarioFerias != null) {
					if (!pessoasNoPreFeed.contains(usuarioFerias)) {
						// se não possui, inclui no preFeed
						preFeedDAO.insertPerson(prefeedtable, usuarioFerias);
						pessoasNoPreFeed.add(usuarioFerias);

					} else {
						// verifica se teve alteração
						User usuarioNoIGI = mapaPessoas.get(usuarioFerias.getFUPessoaID());

						if (!Utils.checkEquals(usuarioNoIGI, usuarioFerias)) {
							// altera
							preFeedDAO.updatePerson(prefeedtable, usuarioFerias);
						}
					}
				} else {
					// alterar para pegar order by
					String query_ultima_data = "SELECT * FROM " + fwpinstancia + "." + fpwtable	+ " where fupessoaid = ? and OFDtIniOco is not null " + "and OFCODOCORR not in (" + events + ") order by OFDtIniOco desc";

					List<User> listaUltimaData = fpwDao.checksPersonFPW(pessoaIDNoFPW, query_ultima_data, Boolean.FALSE, conn_fpw, null);

					User usuarioUltimaOcorrencia = null;

					if (listaUltimaData.size() > 0) {
						// -- 2a validacao -- Ultima Ocorrencia
						String query_ultima_ocorrencia = "SELECT * FROM " + fwpinstancia + "." + fpwtable + " where fupessoaid = ?  and OFDtIniOco = ?  and OFCODOCORR not in (" + events + ")";

						List<User> listaUltimaOcorrencia = fpwDao.checksPersonFPW(pessoaIDNoFPW, query_ultima_ocorrencia, Boolean.TRUE, conn_fpw, listaUltimaData.get(0).getOFDtIniOco());

						if (listaUltimaOcorrencia.size() > 1) {
							boolean temDesligamento = Utils.temDesligamento(listaUltimaOcorrencia);

							if (temDesligamento) {
								// valida qual ocorrencia maior
								usuarioUltimaOcorrencia = Utils.ocorrenciaValida(pesotable,listaUltimaOcorrencia, preFeedDAO);
							} else {
								// nao tem nenhuma outra validação hoje
								usuarioUltimaOcorrencia = listaUltimaOcorrencia.get(0);
							}
						} else {
							if (listaUltimaOcorrencia.size() > 0) {
								usuarioUltimaOcorrencia = listaUltimaOcorrencia.get(0);
							}
						}
					}

					if (usuarioUltimaOcorrencia != null) {
						if (!pessoasNoPreFeed.contains(usuarioUltimaOcorrencia)) {
							// se não possui, inclui no preFeed
							preFeedDAO.insertPerson(prefeedtable, usuarioUltimaOcorrencia);
							pessoasNoPreFeed.add(usuarioUltimaOcorrencia);
						} else {
							// verifica se teve alteração
							User usuarioNoIGI = mapaPessoas.get(usuarioUltimaOcorrencia.getFUPessoaID());

							if (!Utils.checkEquals(usuarioNoIGI, usuarioUltimaOcorrencia)) {
								// altera
								preFeedDAO.updatePerson(prefeedtable, usuarioUltimaOcorrencia);
							}
						}
					} else {
						// -- 3a validacao, nao atendeu nenhuma das duas anteriores
						String query_geral = "SELECT * FROM " + fwpinstancia + "." + fpwtable + "  where fupessoaid = ? and OFCODOCORR not in (" + events + ") order by OFDtIniOco desc";

						List<User> listaUltimaValidacao = fpwDao.checksPersonFPW(pessoaIDNoFPW, query_geral, Boolean.FALSE, conn_fpw, null);

						if (!listaUltimaValidacao.isEmpty()) {

							User terceiraValidacao = null;

							if (listaUltimaValidacao.size() > 1) {
								boolean temDesligamento = Utils.temDesligamento(listaUltimaValidacao);

								if (temDesligamento) {
									// valida qual ocorrencia maior
									terceiraValidacao = Utils.ocorrenciaValida(pesotable,listaUltimaValidacao, preFeedDAO);
								} else {
									// não tem nenhuma outra validação hoje
									terceiraValidacao = listaUltimaValidacao.get(0);
								}
							} else {
								if (listaUltimaValidacao.size() > 0) {
									terceiraValidacao = listaUltimaValidacao.get(0);
								}
							}

							if (terceiraValidacao != null) {
								if (!pessoasNoPreFeed.contains(terceiraValidacao)) {
									// se não possui, inclui no preFeed
									preFeedDAO.insertPerson(prefeedtable, terceiraValidacao);
									pessoasNoPreFeed.add(terceiraValidacao);
								} else {
									// verifica se teve alteração
									User usuarioNoIGI = mapaPessoas.get(terceiraValidacao.getFUPessoaID());

									if (!Utils.checkEquals(usuarioNoIGI, terceiraValidacao)) {
										// altera
										preFeedDAO.updatePerson(prefeedtable, terceiraValidacao);
									}
								}
							}
						} else {
							// Não pegou em nenhuma ocorrencia, pega o ultimo valor sem filtro
							String query_excecao = "SELECT * FROM " + fwpinstancia + "." + fpwtable + "  where fupessoaid = ? order by OFDtIniOco desc";

							List<User> listaExcecao = fpwDao.checksPersonFPW(pessoaIDNoFPW, query_excecao, Boolean.FALSE, conn_fpw, null);

							User ultimaValidacao = null;

							if (listaExcecao.size() > 1) {
								boolean temDesligamento = Utils.temDesligamento(listaExcecao);

								if (temDesligamento) {
									// valida qual ocorrencia maior
									ultimaValidacao = Utils.ocorrenciaValida(pesotable,listaExcecao, preFeedDAO);
								} else {
									// não tem nenhuma outra validação hoje
									ultimaValidacao = listaExcecao.get(0);
								}
							} else {
								if (listaExcecao.size() > 0) {
									ultimaValidacao = listaExcecao.get(0);
								}
							}

							if (ultimaValidacao != null) {
								if (!pessoasNoPreFeed.contains(ultimaValidacao)) {
									// se não possui, inclui no preFeed
									preFeedDAO.insertPerson(prefeedtable, ultimaValidacao);
									pessoasNoPreFeed.add(ultimaValidacao);
								} else {
									// verifica se teve alteração
									User usuarioNoIGI = mapaPessoas.get(ultimaValidacao.getFUPessoaID());

									if (!Utils.checkEquals(usuarioNoIGI, ultimaValidacao)) {
										// altera
										preFeedDAO.updatePerson(prefeedtable, ultimaValidacao);
									}
								}
							}
						}
					}
				}
			}
		} 
		catch (Exception e) {
			logError("Exception selectEntries: " + e);
			throw new ConnectorException("Exception selectEntries: " + e.getMessage());
		} 
		finally {
			try {
				if (conn_fpw != null) {
					conn_fpw.close();
				}
				if (conn_igi != null) {

					conn_igi.close();
				}
			} 
			catch (Exception e) {
				logError("Exception " + e.getMessage());
			}
		}
	}

	public Entry getNextEntry() throws Exception {
		try {
			logmsg("getNextEntry PreFeedFPWConnector");
			Entry var1 = null;
			return var1;
		} 
		catch (Exception e) {
			logError("Exception getNextEntry: " + e);
			throw new ConnectorException("Exception getNextEntry: " + e.getMessage());
		}
	}

	public void querySchema() throws Exception { }

	public void modifyUser(Entry entry) throws Exception { }

	public void deleteEntry(Entry entry, SearchCriteria search) throws Exception { }

	public void putEntry(Entry entry) throws Exception { }

	public Entry queryReply(Entry entry) {
		return null;
	}

	@Override
	public String getVersion() {
		logmsg(COPYRIGHT);

		if (VERSION_INFO == null)
			try {
				String str = getClass().getName().replace(".", "/") + ".class";
				URL uRL = getClass().getClassLoader().getResource(str);
				String[] arrayOfString = uRL.toExternalForm().split("!");
				uRL = new URL(arrayOfString[0].replaceFirst("jar:", ""));
				JarInputStream jarInputStream = new JarInputStream(uRL.openStream(), false);
				Attributes attributes = jarInputStream.getManifest().getMainAttributes();
				VERSION_INFO = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
			} 
			catch (Exception e) {
				logError("Fail to determine the version information: " + e.getMessage());
			}
		return VERSION_INFO;
	}
}
