package com.vd.imageupdater;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.vd.util.DBHelper;
import com.vd.util.WooCommerceUtil;

public class UpdateImages {
	Connection conn = null;
	String projDir = "";
	String uploadsUrl = "";

	public UpdateImages() {
		conn = DBHelper.getConnection();
		projDir = WooCommerceUtil.getProjDir();
		uploadsUrl = WooCommerceUtil.getUploadsUrl();
	}

	public static void main(String args[]) throws Exception {
		UpdateImages updateImg = new UpdateImages();
		updateImg.readProducts();
	}

	public void readProducts() throws Exception {
		String sql = "SELECT Post_ID,Meta_Value FROM WP_PostMeta WHERE meta_key LIKE '_sku' AND Meta_Value like 'TM%'";
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		try {
			preparedStmt = conn.prepareStatement(sql);
			rs = preparedStmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("Post_ID");
				String value = rs.getString("Meta_Value");
				System.out.println("*****************************************");
				System.out.println(id + " " + value);
				if (readImageUrl(value, id)) {
					System.out.println("Images Successfully Updated");
				} else {
					System.out.println("Not able to Update Images");
				}
				System.out.println("*****************************************");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			rs = null;
			preparedStmt = null;	
		}
	}

	public boolean readImageUrl(String sku, int product_id) throws Exception {
		String sql = "SELECT PIMG.ID,PIMG.Guid FROM WP_Posts P "
				+ " LEFT JOIN WP_PostMeta PMIMG ON P.ID = PMIMG.Post_ID AND ( PMIMG.Meta_key = '_thumbnail_id' OR PMIMG.Meta_key = '_product_image_gallery' ) "
				+ " LEFT JOIN WP_Posts PIMG ON FIND_IN_SET(PIMG.ID, PMIMG.Meta_Value) " + " WHERE P.ID =" + product_id;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		boolean updateFlag = true;
		try {
			preparedStmt = conn.prepareStatement(sql);
			rs = preparedStmt.executeQuery();
			int i = 10001;
			while (rs.next()) {
				String url = rs.getString("Guid");
				if (url == null || url.equals("")) {
					continue;
				}

				String ext = url.substring(url.lastIndexOf("."));
				String oldImgPathWithName = projDir + url.replace("http://localhost/", "");
				String imgPath = oldImgPathWithName.substring(0, oldImgPathWithName.lastIndexOf("/"));
				String newImgPathWithName = imgPath + "/" + sku + "_" + i + ext;
				System.out.println(newImgPathWithName);
				if (renameFile(oldImgPathWithName, newImgPathWithName)) {
					String newUrl = url.substring(0, url.lastIndexOf("/"));
					newUrl += "/" + sku + "_" + i + ext;
					int imgID = rs.getInt("ID");
					updateFlag = updateImgUrl(imgID, newUrl, newImgPathWithName);
				}
				i++;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		} finally {
			rs = null;
			preparedStmt = null;
		}
		return updateFlag;
	}

	public boolean updateImgUrl(int postID, String url, String newImgPathWithName) throws Exception {
		String sql = "UPDATE WP_Posts set Guid = ? WHERE ID = ?";
		PreparedStatement preparedStmt = null;
		try {
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, url);
			preparedStmt.setInt(2, postID);
			preparedStmt.executeUpdate();

			String relativeFilePath = url.replaceAll(uploadsUrl, "");
			updateImageRelativePath(postID, relativeFilePath);

			readImageMetaData(postID, newImgPathWithName);

		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		} finally {
			preparedStmt = null;
		}
		return true;
	}

	public boolean updateImageRelativePath(int postID, String relativeFilePath) throws Exception {
		String sql = "UPDATE WP_PostMeta SET Meta_Value =? " + " WHERE Post_ID = ? AND Meta_Key = '_wp_attached_file'";
		PreparedStatement preparedStmt = null;
		try {
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, relativeFilePath);
			preparedStmt.setInt(2, postID);
			int ans = preparedStmt.executeUpdate();
			if (ans > 0) {
				return true;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		} finally {
			preparedStmt = null;
		}
		return false;
	}

	public boolean readImageMetaData(int postID, String newImgPathWithName) throws Exception {
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM WP_PostMeta WHERE Post_ID = ? AND Meta_Key = '_wp_attachment_metadata'";
		try {
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setInt(1, postID);
			rs = preparedStmt.executeQuery();
			while (rs.next()) {
				String mValue = rs.getString("Meta_Value");
				System.out.println("Old :" + mValue);
				String newMetaValue = generateNewMetaData(mValue, newImgPathWithName);
				System.out.println("New : " + newMetaValue);
				updateImageMetaData(postID, newMetaValue);
			}
		} catch (Exception e) {
			System.err.println("Got an exception!");
			System.err.println(e.getMessage());
			return false;
		} finally {
			rs = null;
			preparedStmt = null;
		}
		return false;
	}

	public static String generateNewMetaData(String newMetaValue, String newImgPathWithName) {
		String returnStr = "";
		String strPart1 = "";
		String strPart2 = "";
		String strPart3 = "";
		String strPart4 = "";
		String strPart5 = "";
		String tmp = newMetaValue.substring(0, newMetaValue.indexOf(";") + 1);
		newMetaValue = newMetaValue.substring(tmp.length());
		String tmp1 = newMetaValue.substring(0, newMetaValue.indexOf(";") + 1);
		newMetaValue = newMetaValue.substring(tmp1.length());
		strPart1 = tmp + tmp1;
		returnStr += strPart1;

		tmp = newMetaValue.substring(0, newMetaValue.indexOf(";") + 1);
		newMetaValue = newMetaValue.substring(tmp.length());
		tmp1 = newMetaValue.substring(0, newMetaValue.indexOf(";") + 1);
		newMetaValue = newMetaValue.substring(tmp1.length());
		strPart2 = tmp + tmp1;
		returnStr += strPart2;

		tmp = newMetaValue.substring(0, newMetaValue.indexOf(";") + 1);
		newMetaValue = newMetaValue.substring(tmp.length());
		tmp1 = newMetaValue.substring(0, newMetaValue.indexOf(";") + 1);
		newMetaValue = newMetaValue.substring(tmp1.length());
		strPart3 = tmp + tmp1;

		String fileName = newImgPathWithName.substring(newImgPathWithName.lastIndexOf("/") + 1);

		String newStrPart3 = "s:4:\"file\";s:";
		strPart3 = strPart3.replace(newStrPart3, "");
		strPart3 = strPart3.substring(strPart3.indexOf(":") + 1);
		String ts = strPart3.substring(0, strPart3.lastIndexOf("/") + 1);
		ts += fileName + "\"";
		newStrPart3 += (ts.length() - 2) + ":" + ts;
		returnStr += newStrPart3 + ";";

		tmp = newMetaValue.substring(0, newMetaValue.indexOf(";") + 1);
		newMetaValue = newMetaValue.substring(tmp.length());
		tmp1 = newMetaValue.substring(0, newMetaValue.indexOf("}}") + 2);
		newMetaValue = newMetaValue.substring(tmp1.length());
		strPart4 = tmp + tmp1;

		returnStr += parseRelatedFilesFromStr(strPart4, newImgPathWithName);
		strPart5 = newMetaValue;
		returnStr += strPart5;

		return returnStr;
	}

	public boolean updateImageMetaData(int postID, String value) throws Exception {
		String sql = "UPDATE WP_PostMeta SET Meta_Value =? "
				+ " WHERE Post_ID = ? AND Meta_Key = '_wp_attachment_metadata'";
		PreparedStatement preparedStmt = null;
		try {
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, value);
			preparedStmt.setInt(2, postID);
			int ans = preparedStmt.executeUpdate();
			if (ans > 0) {
				return true;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		} finally {
			preparedStmt = null;
		}
		return false;
	}

	public static String parseRelatedFilesFromStr(String str, String newImgPathWithName) {
		String returnStr = "";
		String tmp = str.substring(0, str.indexOf("{") + 1);
		returnStr += tmp;
		str = str.substring(tmp.length());
		String t = "";
		do {
			t = str.substring(0, str.indexOf("}") + 1);
			if (t.length() > 10) {
				returnStr += renameRelatedFiles(t, newImgPathWithName);
			} else {
				returnStr += t;
			}
			str = str.substring(t.length());
		} while (t.length() > 2);
		return returnStr;
	}

	public static String renameRelatedFiles(String str, String updateFilePath) {

		String fileName = updateFilePath.substring(updateFilePath.lastIndexOf("/") + 1);
		String path = updateFilePath.replace(fileName, "");
		String ext = fileName.substring(fileName.lastIndexOf("."));
		fileName = fileName.replace(ext, "");
		String sameStr = str;
		String finalReturnStr = "";
		String tmp = str.substring(0, str.indexOf("{"));
		finalReturnStr += tmp;
		str = str.replace(tmp, "");
		if (str.length() > 1) {
			String tmpArr[] = str.split(";");
			int height = 0;
			int width = 0;
			String file = "";
			boolean hFlag = false;
			boolean wFlag = false;
			boolean fFlag = false;
			for (String tStr : tmpArr) {
				if (hFlag) {
					hFlag = false;
					height = Integer.parseInt(tStr.substring(tStr.indexOf(":") + 1));
				}
				if (wFlag) {
					wFlag = false;
					width = Integer.parseInt(tStr.substring(tStr.indexOf(":") + 1));
				}
				if (fFlag) {
					fFlag = false;
					file = tStr.substring(tStr.indexOf(":") + 1);
					file = file.substring(file.indexOf(":") + 1);

				}
				if (tStr.contains("s:6:\"height\"")) {
					hFlag = true;
				}
				if (tStr.contains("s:5:\"width\"")) {
					wFlag = true;
				}
				if (tStr.contains("s:4:\"file\"")) {
					fFlag = true;
				}
			}

			for (String tStr : tmpArr) {
				if (tStr.contains(file)) {
					String tmpStr = "-" + width + "x" + height + ext;
					tmpStr = fileName + tmpStr;
					String newFilePath = tmpStr;
					tmpStr = "s:" + tmpStr.length() + ":\"" + tmpStr + "\"";
					finalReturnStr += tmpStr + ";";
					file = file.replaceAll("\"", "");
					String oldFilePath = path + file;
					newFilePath = path + newFilePath;
					renameFile(oldFilePath, newFilePath);
				} else
					finalReturnStr += tStr + (tStr.endsWith("}") ? "" : ";");
			}
		} else {
			return sameStr;
		}
		return finalReturnStr;

	}

	public static boolean renameFile(String strOldFile, String strNewFile) {
		File file = new File(strOldFile);
		File newFile = new File(strNewFile);
		if (file.renameTo(newFile)) {
			return true;
		} else {
			return false;
		}
	}

}
