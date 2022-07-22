package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CgvDAO {
	Connection conn;
	PreparedStatement pstmt;
	final String sql_insert = "INSERT INTO CGV VALUES((SELECT NVL(MAX(CID),0)+1 FROM CGV),?,?,?,?)"; // 크롤링하여 받아온 데이터 저장
	final String sql_update = "UPDATE CGV SET BOOKCNT = BOOKCNT + ? WHERE CID=?"; // 예매횟수 변경
	final String sql_selectOne = "SELECT * FROM CGV WHERE CID=?"; 
	final String sql_selectAll = "SELECT * FROM CGV WHERE TITLE LIKE '%'||?||'%' AND GENRE LIKE '%'||?||'%' ORDER BY TITLE ASC"; // 영화제목 영화장르 검색 or 전체목록 출력
	final String sql_sample = "SELECT COUNT(*) AS CNT FROM CGV"; // 샘플데이터 확인 
	final String sql_delete = "DELETE FROM CGV WHERE CID=?"; // 영화 삭제
	
	public boolean insert(CgvVO vo) { 
		conn = JDBCUtil.connect(); // 드라이버 연결
		try {
			pstmt = conn.prepareStatement(sql_insert); // 데이터 insert
			pstmt.setString(1, vo.getTitle()); // 영화제목
			pstmt.setString(2, vo.getImage()); // 영화포스터
			pstmt.setString(3, vo.getGenre()); // 장르
			pstmt.setInt(4, vo.getBookcnt()); // 예매횟수
			pstmt.executeUpdate(); // 쿼리문 실행
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			JDBCUtil.disconnect(pstmt, conn); // 드라이버 연결 해제
		}
		return true;

	}

	public boolean update(CgvVO vo) { // 예매횟수 update 
		conn = JDBCUtil.connect();
		try {
			pstmt = conn.prepareStatement(sql_update); 
			pstmt.setInt(1, vo.getBookcnt()); // 예매횟수
			pstmt.setInt(2, vo.getCid()); // pk
			int res = pstmt.executeUpdate();
			if (res == 0) {
				System.out.println("로그 : update 실패");
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			JDBCUtil.disconnect(pstmt, conn);
		}
		return true;
	}


	public CgvVO selectOne(CgvVO vo) {
		conn = JDBCUtil.connect();
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql_selectOne);
			pstmt.setInt(1, vo.getCid());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				CgvVO data = new CgvVO();
				data.setTitle(rs.getString("TITLE"));
				data.setImage(rs.getString("IMAGE"));
				data.setGenre(rs.getString("GENRE"));
				data.setBookcnt(rs.getInt("BOOKCNT"));
				return data;
			} else {
				return null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JDBCUtil.disconnect(pstmt, conn);
		}
	}

	public ArrayList<CgvVO> selectAll(CgvVO vo) { // 전체 영화목록 출력 or 검색(장르, 이름)
		ArrayList<CgvVO> datas = new ArrayList<CgvVO>(); // selectAll은 배열리스트 반환
		conn = JDBCUtil.connect();
		try {
			pstmt = conn.prepareStatement(sql_selectAll);
			pstmt.setString(1, vo.getTitle()); // 이름
			pstmt.setString(2, vo.getGenre()); // 장르
			ResultSet rs = pstmt.executeQuery(); // 실행 결과 = rs
			while (rs.next()) { // rs의 다음 값이 없을 때까지 실행 
				CgvVO data = new CgvVO();
				data.setCid(rs.getInt("CID"));
				data.setTitle(rs.getString("TITLE"));
				data.setImage(rs.getString("IMAGE"));
				data.setGenre(rs.getString("GENRE"));
				data.setBookcnt(rs.getInt("BOOKCNT"));
				datas.add(data);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return datas;
	}

	public boolean hasSample(CgvVO vo) {
		conn = JDBCUtil.connect();
		try {
			pstmt = conn.prepareStatement(sql_sample);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			int cnt = rs.getInt("CNT");
			if (cnt >= 5) { // 5개 이상의 데이터를 가지고 있으면
				return true;
			}
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			JDBCUtil.disconnect(pstmt, conn);
		}
	}
	
	public boolean delete(CgvVO vo) {
		conn = JDBCUtil.connect();
		try {
			pstmt = conn.prepareStatement(sql_delete);
			pstmt.setInt(1, vo.getCid()); // 삭제할 영화 pk 
			int res = pstmt.executeUpdate();
			if (res == 0) { 
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			JDBCUtil.disconnect(pstmt, conn);
		}
		return true;
	}
}
