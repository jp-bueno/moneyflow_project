package model.dao;

import java.security.Key;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;

import model.dao.ConexaoBD;
import model.vo.MovimentacaoVO;
import model.vo.RelatorioAnualVO;
import model.vo.UsuarioVO;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class UsuarioDAO {
	
	private static final String CHAVE = "0qoVmtNcUUvhLhPZ";
    
    public boolean verificaUsuario(UsuarioVO usuarioVO) {
    	
    	String senhaEncriptografada = criptografar(usuarioVO.getSenha());
    	
    	int qtd = 0;

        String comandoSQL = "SELECT count(*) FROM usuario WHERE email = '"
        		+ usuarioVO.getEmail()
        		+ "' AND senha = '"
        		+ senhaEncriptografada
        		+ "';";

        try (Statement comando = ConexaoBD.getConexaoBD().createStatement();
             ResultSet resultado = comando.executeQuery(comandoSQL)) {

            while (resultado.next()) {
                qtd = resultado.getInt(1);
            }
            
            resultado.close();
            comando.close();

        } catch (SQLException e) {
            System.err.println("Erro ao realizar conexão com o banco de dados.");
            e.printStackTrace();
        }

        if(qtd != 0)
        	return true;
        else
        	return false;
    }
    
    public boolean verificaUsuario(String email) {
    	
    	int qtd = 0;

        String comandoSQL = "SELECT count(*) FROM usuario WHERE email = '"
        		+ email
        		+ "';";

        try (Statement comando = ConexaoBD.getConexaoBD().createStatement();
             ResultSet resultado = comando.executeQuery(comandoSQL)) {

            while (resultado.next()) {
                qtd = resultado.getInt(1);
            }
            
            resultado.close();
            comando.close();

        } catch (SQLException e) {
            System.err.println("Erro ao realizar conexão com o banco de dados.");
            e.printStackTrace();
        }

        if(qtd != 0)
        	return true;
        else
        	return false;
    }

    public boolean addUsuario(UsuarioVO usuarioVO) {
    	
    	int linhasAfetadas = 0;

        String comandoSQL = "INSERT INTO usuario (email, senha, nome) VALUES (?, ?, ?);";

        try (PreparedStatement comando = ConexaoBD.getConexaoBD().prepareStatement(comandoSQL)) {
            comando.setString(1, usuarioVO.getEmail());
            comando.setString(2, criptografar(usuarioVO.getSenha()));
            comando.setString(3, usuarioVO.getNome());

            linhasAfetadas = comando.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao adicionar usuário no banco de dados.");
            e.printStackTrace();
        }
        
        if(linhasAfetadas > 0)
        	return true;
    	else
    		return false;        		
    }

    private String criptografar(String senha) {
        try {

            SecretKey chaveSecreta = new SecretKeySpec(CHAVE.getBytes(), "AES");
            Cipher cifra = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cifra.init(Cipher.ENCRYPT_MODE, chaveSecreta);


            byte[] textoCriptografado = cifra.doFinal(new String(senha).getBytes());

            return Base64.getEncoder().encodeToString(textoCriptografado);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

	public ArrayList<RelatorioAnualVO> getRelatorioAnual(String emailUsuario) {
		RelatorioAnualVO relatorioVO;
		ArrayList<RelatorioAnualVO> dados = new ArrayList<RelatorioAnualVO>();

		String comandoSQL = "SELECT MONTH(m.data_ocorrencia) AS mes, SUM(m.valor_total) AS valorTotal FROM movimentacao m JOIN usuario u ON m.emailUsuario = u.email WHERE YEAR(m.data_ocorrencia) = YEAR(CURRENT_DATE()) AND u.email = '"
				+ emailUsuario
				+ "' GROUP BY MONTH(m.data_ocorrencia) ORDER BY mes;";

		try {
			Statement comando = ConexaoBD.getConexaoBD().createStatement();
			ResultSet resultado = comando.executeQuery(comandoSQL);

			while(resultado.next()) {				
				relatorioVO = new RelatorioAnualVO();
				relatorioVO.setNumeroMes(resultado.getInt("mes"));
				relatorioVO.setValorTotalMes(resultado.getFloat("valorTotal"));
				
				dados.add(relatorioVO);
			}

			resultado.close();
			comando.close();

		}catch (SQLException e) {
			System.err.println("Erro ao realizar conexao com o banco "
					+ "verifique a url de conexão");
			e.printStackTrace();
		}

		return dados;
	}
}
