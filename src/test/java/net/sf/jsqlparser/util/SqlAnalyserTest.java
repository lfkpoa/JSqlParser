/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class SqlAnalyserTest {

  @Test
  public void testGetTableList() throws Exception {

    SqlChecker.withSql("SELECT COUNT(*) FROM TABELA").checkRead(/* esperado */"TABELA")
        .checkWrite();

    SqlChecker.withSql("DROP TABLE IF EXISTS DIME.TABELA").checkRead()
        .checkWrite(/* esperado */"DIME.TABELA");

    SqlChecker.withSql("DROP TABLE TABELA").checkRead().checkWrite(/* esperado */"TABELA");


    SqlChecker.withSql("TRUNCATE TABLE TABELA").checkRead().checkWrite(/* esperado */"TABELA");

    SqlChecker.withSql("DESCRIBE TABELA").checkRead(/* esperado */"TABELA").checkWrite();

    SqlChecker.withSql("CREATE TABLE X (COL1 STRING)").checkRead().checkWrite(/* esperado */"X");

    SqlChecker.withSql("CREATE TABLE X (COL1 STRING) STORED AS PARQUET").checkRead()
        .checkWrite(/* esperado */"X");

    SqlChecker.withSql("CREATE TABLE X AS SELECT * FROM TABELA").checkRead("TABELA")
        .checkWrite(/* esperado */"X");

    SqlChecker.withSql(
        "WITH X AS (SELECT * FROM TABELA) SELECT * FROM TABELA2 WHERE X.NAO_E_TABELA=TABELA2.NAO_E_TABELA")
        .checkRead(/* esperado */"TABELA", "TABELA2").checkWrite();

    SqlChecker.withSql("SELECT COUNT(*) FROM TABELA, TABELA2 WHERE TABELA.X=TABELA2.X")
        .checkRead(/* esperado */"TABELA", "TABELA2").checkWrite();

    SqlChecker.withSql("SELECT COUNT(*) FROM TABELA LEFT JOIN TABELA2 ON TABELA.X=TABELA2.X")
        .checkRead(/* esperado */"TABELA", "TABELA2").checkWrite();

    SqlChecker.withSql("SELECT COUNT(*) FROM TABELA LEFT ANTI JOIN TABELA2 ON TABELA.X=TABELA2.X")
        .checkRead(/* esperado */"TABELA", "TABELA2").checkWrite();

    SqlChecker.withSql("SELECT COUNT(*) FROM TABELA LEFT SEMI JOIN TABELA2 ON TABELA.X=TABELA2.X")
        .checkRead(/* esperado */"TABELA", "TABELA2").checkWrite();

    SqlChecker.withSql(
        "SELECT COUNT(*) FROM TABELA, (SELECT * FROM TABELA2 WHERE A='123') AS NAO_TABELA WHERE TABELA.X=NAO_TABELA.X")
        .checkRead(/* esperado */"TABELA", "TABELA2").checkWrite();

    SqlChecker.withSql("SELECT COUNT(*) FROM SCHEMA.TABELA")
        .checkRead(/* esperado */"SCHEMA.TABELA").checkWrite();

    SqlChecker.withSql("SELECT /* SELECT * FROM PEGADINHA */ COUNT(*) FROM SCHEMA.TABELA")
        .checkRead(/* esperado */"SCHEMA.TABELA").checkWrite();

    SqlChecker.withSql("-- SELECT * FROM PEGADINHA\nSELECT COUNT(*) FROM SCHEMA.TABELA")
        .checkRead(/* esperado */"SCHEMA.TABELA").checkWrite();

    SqlChecker
        .withSql("SELECT COUNT(*) FROM SCHEMA.TABELA LEFT JOIN DIME.TABELA2 ON TABELA.X=TABELA2.Y")
        .checkRead(/* esperado */"SCHEMA.TABELA", "DIME.TABELA2").checkWrite();

    SqlChecker.withSql("SELECT COUNT(*) FROM SCHEMA.TABELA, TABELA.COMPLEX_TYPE")
        .checkRead(/* esperado */"SCHEMA.TABELA", "TABELA.COMPLEX_TYPE").checkWrite();

    SqlChecker
        .withSql(
            "SELECT COUNT(*) FROM SCHEMA.TABELA, TABELA.COMPLEX_TYPE, COMPLEX_TYPE.COMPLEX_TYPE2")
        .checkRead(/* esperado */"SCHEMA.TABELA", "TABELA.COMPLEX_TYPE",
            "COMPLEX_TYPE.COMPLEX_TYPE2")
        .checkWrite();

    SqlChecker.withSql("CREATE VIEW X AS SELECT * FROM TABELA").checkRead("TABELA")
        .checkWrite(/* esperado */"X");

    SqlChecker.withSql("ALTER VIEW X AS SELECT * FROM TABELA").checkRead("TABELA")
        .checkWrite(/* esperado */"X");

    SqlChecker.withSql("COMPUTE STATS TABELA").checkRead(/* esperado */"TABELA").checkWrite();

    SqlChecker.withSql("grant select on table contagil.xyz to u57519110087").checkRead()
        .checkWrite("contagil.xyz");

    // Não aceita esta sintaxe
    // SqlChecker.withSql("ALTER TABLE contagil.xyz RENAME TO u57519110087.xyz")
    // .checkRead()
    // .checkWrite("contagil.xyz");

    SqlChecker.withSql("INSERT INTO contagil.xyz VALUES('UM',2)").checkRead()
        .checkWrite("contagil.xyz");

    SqlChecker.withSql("INSERT INTO contagil.xyz SELECT * FROM contagil.tabela")
        .checkRead("contagil.tabela").checkWrite("contagil.xyz");

    SqlChecker.withSql("DESCRIBE contagil.tabela").checkRead("contagil.tabela").checkWrite();

    SqlChecker.withSql(
        "CREATE VIEW contagil.MY_VIEW AS SELECT * FROM contagil.TABELA1, contagil.TABELA2 using(id) WHERE X>0")
        .checkRead("contagil.TABELA1", "contagil.TABELA2").checkWrite("contagil.MY_VIEW");

    SqlChecker.withSql("REVOKE SELECT ON TABLE CONTAGIL.XYZ FROM U57519110087").checkRead()
        .checkWrite("CONTAGIL.XYZ");

    SqlChecker.withSql("UPSERT INTO CONTAGIL.XYZ SELECT * FROM DIME.TABELA")
        .checkRead("DIME.TABELA").checkWrite("CONTAGIL.XYZ");

    SqlChecker
        .withSql("SELECT CONCAT('[',CASO,']')\n" + "FROM CONTAGIL.PROC_CT_CATEG \n"
            + "WHERE CASO LIKE '%combate%' AND CASO LIKE '%fraude%'\n" + "LIMIT 1")
        .checkRead("CONTAGIL.PROC_CT_CATEG").checkWrite();

    SqlChecker.withSql("SELECT\n"
        + "           regexp_replace(a11.DD_FISC_DLAN_PROCESSO,'[^D]','')  AS PROC,\n"
        + "           MAX(NB_FISC_PROG_CASO_ESPECIAL) AS CASO\n"
        + "       from    fisc.WF_FISC_DLAN   a11\n"
        + "           join    fisc.WD_FISC_PROG_CASO_ESPECIAL a12\n"
        + "             on    (a11.NR_FISC_PROG_CASO_ESPECIAL = a12.NR_FISC_PROG_CASO_ESPECIAL)\n"
        + "       group by    1\n" + "LIMIT 10")
        .checkRead("fisc.WF_FISC_DLAN", "fisc.WD_FISC_PROG_CASO_ESPECIAL").checkWrite();

    SqlChecker.withSql("show create view contagil.view")
        .checkRead("contagil.view")
        .checkWrite();
     
  }

  /**
   * Testa a função 'getTableList'
   */
  @Test
  public void testGetTableList2() throws Exception {

    SqlChecker.withSql("SELECT\n" + "SUBSTR(WF_EPRO_RESQ.DD_EPRO_ESTQ_PROCESSO,12,4) AS ANO,\n"
        + "   TAB_TIPO_UNIDADE.SG_EPRO_TP_UNIDADE,\n" + "  TAB_UA_NIVEL2.SG_UA_NIVEL2,\n"
        + "  TAB_UA_NIVEL4.SG_UA_NIVEL4,\n" + "  TAB_JULG_RES1.NM_EPRO_RESQ_RES_JULG,\n"
        + "  TAB_JULG_RES2.NM_EPRO_RESQ_RES_JULG,\n"
        + "  COUNT(DISTINCT WF_EPRO_RESQ.DD_EPRO_ESTQ_PROCESSO)\n" + "FROM\n"
        + "  proc.WF_EPRO_RESQ\n"
        + "  LEFT JOIN PROC.WF_EPRO_EQIP AS WF_EPRO_RESQ_NR_EPRO_VERSAO_WF_EPRO_EQIP ON WF_EPRO_RESQ.NR_EPRO_VERSAO = WF_EPRO_RESQ_NR_EPRO_VERSAO_WF_EPRO_EQIP.NR_EPRO_VERSAO\n"
        + "  AND WF_EPRO_RESQ.NR_EPRO_EQIP = WF_EPRO_RESQ_NR_EPRO_VERSAO_WF_EPRO_EQIP.NR_EPRO_EQIP\n"
        + "  LEFT JOIN PROC.WD_EPRO_RESQ_RES_JULG AS TAB_JULG_RES1 ON WF_EPRO_RESQ.NR_EPRO_RESQ_JULG_NVL1 = TAB_JULG_RES1.NR_EPRO_RESQ_RES_JULG\n"
        + "  LEFT JOIN PROC.WD_EPRO_RESQ_RES_JULG AS TAB_JULG_RES2 ON WF_EPRO_RESQ.NR_EPRO_RESQ_JULG_NVL2 = TAB_JULG_RES2.NR_EPRO_RESQ_RES_JULG\n"
        + "  LEFT JOIN PROC.WD_EPRO_RESQ_TIPO_VOTACAO AS TAB_TIPO_VOTACAO ON WF_EPRO_RESQ.NR_EPRO_RESQ_TP_VOTACAO = TAB_TIPO_VOTACAO.NR_EPRO_RESQ_TIPO_VOTACAO\n"
        + "  LEFT JOIN PROC.WD_EPRO_EQIP_EQUIPE AS TAB_EPRO_EQUIPE ON WF_EPRO_RESQ_NR_EPRO_VERSAO_WF_EPRO_EQIP.NR_EPRO_EQIP_EQUIPE = TAB_EPRO_EQUIPE.NR_EPRO_EQIP_EQUIPE\n"
        + "  LEFT JOIN PROC.WD_EPRO_EQIP_EQUIPE_N4 AS TAB_EPRO_EQUIPE_N4 ON TAB_EPRO_EQUIPE.NR_EPRO_EQIP_EQUIPE_N4 = TAB_EPRO_EQUIPE_N4.NR_EPRO_EQIP_EQUIPE_N4\n"
        + "  LEFT JOIN PROC.WD_EPRO_EQIP_EQUIPE_N1 AS TAB_EPRO_EQUIPE_N1 ON TAB_EPRO_EQUIPE_N4.NR_EPRO_EQIP_EQUIPE_N1 = TAB_EPRO_EQUIPE_N1.NR_EPRO_EQIP_EQUIPE_N1\n"
        + "  LEFT JOIN PROC.WD_EPRO_UNIDADE AS TAB_EPRO_UNIDADE ON TAB_EPRO_EQUIPE_N1.NR_EPRO_UNIDADE = TAB_EPRO_UNIDADE.NR_EPRO_UNIDADE\n"
        + "  LEFT JOIN PROC.WD_EPRO_TP_UNIDADE AS TAB_TIPO_UNIDADE ON TAB_EPRO_UNIDADE.NR_EPRO_TP_UNIDADE = TAB_TIPO_UNIDADE.NR_EPRO_TP_UNIDADE\n"
        + "  LEFT JOIN DIME.WD_UAS_NIVEL4 AS TAB_UA_NIVEL4 ON TAB_EPRO_UNIDADE.NR_UA_NIVEL4_EPUN = TAB_UA_NIVEL4.NR_UA_NIVEL4\n"
        + "  LEFT JOIN DIME.WD_UAS_NIVEL2 AS TAB_UA_NIVEL2 ON TAB_UA_NIVEL4.NR_UA_NIVEL2 = TAB_UA_NIVEL2.NR_UA_NIVEL2\n"
        + "WHERE\n" + "  SUBSTR(WF_EPRO_RESQ.DD_EPRO_ESTQ_PROCESSO, 1, 2) <> '00'\n"
        + "  AND WF_EPRO_RESQ.DD_EPRO_ESTQ_PROCESSO IREGEXP '\\\\d+'\n"
        + "  AND TAB_TIPO_UNIDADE.SG_EPRO_TP_UNIDADE = 'DRJ'\n"
        + "  AND TAB_JULG_RES1.NM_EPRO_RESQ_RES_JULG ILIKE 'Recurso Volun%'\n" + "  AND COALESCE(\n"
        + "    TAB_JULG_RES1.NM_EPRO_RESQ_RES_JULG,\n"
        + "    TAB_JULG_RES2.NM_EPRO_RESQ_RES_JULG,\n"
        + "    TAB_TIPO_VOTACAO.NM_EPRO_RESQ_TIPO_VOTACAO\n" + "  ) IS NOT NULL\n"
        + "GROUP BY 1,2,3,4,5,6")
        .checkRead("proc.WF_EPRO_RESQ", "PROC.WF_EPRO_EQIP", "PROC.WD_EPRO_RESQ_RES_JULG",
            "PROC.WD_EPRO_RESQ_TIPO_VOTACAO", "PROC.WD_EPRO_EQIP_EQUIPE",
            "PROC.WD_EPRO_EQIP_EQUIPE_N4", "PROC.WD_EPRO_EQIP_EQUIPE_N1", "PROC.WD_EPRO_UNIDADE",
            "PROC.WD_EPRO_TP_UNIDADE", "DIME.WD_UAS_NIVEL4", "DIME.WD_UAS_NIVEL2")
        .checkWrite();

  }

  /**
   * Testa a função 'getTableList'
   */
  @Test
  public void testGetTableList3() throws Exception {

    SqlChecker.withSql("SHOW CREATE VIEW CENTRAL_GESTAO.VISAO_PROC_MALHA_AGREG")
        .checkRead("CENTRAL_GESTAO.VISAO_PROC_MALHA_AGREG").checkWrite();

  }

  public static void comparaListas(List<String> listaObtida, String... valoresEsperados)
      throws Exception {
    Set<String> valoresObtidos = (listaObtida == null) ? Collections.emptySet()
        : listaObtida.stream()
            .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));

    for (String valorEsperado : valoresEsperados) {
      if (!valoresObtidos.contains(valorEsperado)) {
        throw new Exception("Não encontrou referência a '" + valorEsperado + "'! Valores obtidos: "
            + String.join(", ", valoresObtidos));
      }
    }

    Set<String> valoresEsperadosSet = Arrays.stream(valoresEsperados)
        .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));

    for (String valorObtido : valoresObtidos) {
      if (!valoresEsperadosSet.contains(valorObtido)) {
        throw new Exception("Encontrou referência excedente '" + valorObtido
            + "'! Valores esperados: " + String.join(", ", valoresObtidos));
      }
    }
  }

  public static class SqlChecker {
    private SqlAnalyser analyser;

    public SqlChecker(String sql) throws Exception {
      analyser = new SqlAnalyser();
      analyser.analyse(sql);
    }

    public static SqlChecker withSql(String sql) throws Exception {
      return new SqlChecker(sql);
    }

    public SqlChecker checkRead(String... valoresEsperados) throws Exception {
      comparaListas(analyser.getTablesRead(), valoresEsperados);
      return this;
    }

    public SqlChecker checkWrite(String... valoresEsperados) throws Exception {
      comparaListas(analyser.getTablesWrite(), valoresEsperados);
      return this;
    }

  }
}
