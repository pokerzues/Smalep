<?php
function _get($str) {
	$val = !empty($_GET[$str]) ? $_GET[$str] : null;
	return $val;
} 
if (_get('token') == "sp199539") {
	$type = _get('type');
	if ($type == 0) {
		$mysql_server_name = "localhost";
		$mysql_username = "root";
		$mysql_password = "ede1954b";
		$con = mysql_connect($mysql_server_name, $mysql_username, $mysql_password);
		mysql_select_db("server", $con);
		mysql_query("SET NAMES utf8");
		$sql = "SELECT val FROM sensor WHERE id = 0";
		$result = mysql_query($sql);
		while ($row = mysql_fetch_array($result)) {
			$temp=$row['val'];
		} 
		$sql = "SELECT val FROM sensor WHERE id = 1";
		$result = mysql_query($sql);
		while ($row = mysql_fetch_array($result)) {
			$humidity=$row['val'];
		} 
		$sql = "SELECT state from ugt WHERE name = 0";
		$result = mysql_query($sql);
		while ($row = mysql_fetch_array($result)) {
			$urgent=$row['state']-1;
		} 
		$sql = "SELECT val from cmd WHERE id = 0";
		$result = mysql_query($sql);
		while ($row = mysql_fetch_array($result)) {
			$cmd0=$row['val']-1;
		} 
		$sql = "SELECT val from cmd WHERE id = 1";
		$result = mysql_query($sql);
		while ($row = mysql_fetch_array($result)) {
			$cmd1=$row['val']-1;
		} 
		$sql = "SELECT val from cmd WHERE id = 2";
		$result = mysql_query($sql);
		while ($row = mysql_fetch_array($result)) {
			$cmd2=$row['val']-1;
		} 
		$sql = "SELECT val from cmd WHERE id = 3";
		$result = mysql_query($sql);
		while ($row = mysql_fetch_array($result)) {
			$cmd3=$row['val']-1;
		} 
		$sql = "SELECT val from cmd WHERE id = 4";
		$result = mysql_query($sql);
		while ($row = mysql_fetch_array($result)) {
			$cmd4=$row['val']-1;
		} 
		$sql = "SELECT val from cmd WHERE id = 5";
		$result = mysql_query($sql);
		while ($row = mysql_fetch_array($result)) {
			$cmd5=$row['val']-1;
		} 
		mysql_close($con);
		echo "{".$temp."+".$humidity."+".$urgent."+".$cmd0."+".$cmd1."+".$cmd2."+".$cmd3."+".$cmd4."+".$cmd5."}";
	} elseif ($type == 1) {
		$no = _get('no');
		$val = _get('val') + 1;
		$mysql_server_name = "localhost";
		$mysql_username = "root";
		$mysql_password = "ede1954b";
		$con = mysql_connect($mysql_server_name, $mysql_username, $mysql_password);
		mysql_select_db("server", $con);
		mysql_query("SET NAMES utf8");
		$datenow = date("Y/m/d H:i:s");
		$sql = "UPDATE cmd SET val='$val' WHERE id = '$no'";
		mysql_query($sql);
		$sql = "UPDATE sensor SET timestamp='$datenow' WHERE id = '$no'";
		mysql_query($sql);
		echo "success";
		mysql_close($con);
	} elseif ($type == 2) {
		$no = _get('no');
		$mysql_server_name = "localhost";
		$mysql_username = "root";
		$mysql_password = "ede1954b";
		$con = mysql_connect($mysql_server_name, $mysql_username, $mysql_password);
		mysql_select_db("server", $con);
		mysql_query("SET NAMES utf8");
		$sql = "SELECT val FROM sensor WHERE id = '$no'";
		$result = mysql_query($sql);
		while ($row = mysql_fetch_array($result)) {
			echo $row['val'];
		} 
		mysql_close($con);
	} elseif ($type == 3) {
		$mysql_server_name = "localhost";
		$mysql_username = "root";
		$mysql_password = "ede1954b";
		$con = mysql_connect($mysql_server_name, $mysql_username, $mysql_password);
		mysql_select_db("server", $con);
		mysql_query("SET NAMES utf8");
		$sql = "SELECT state from ugt WHERE name = 0";
		$result = mysql_query($sql);
		while ($row = mysql_fetch_array($result)) {
			echo $row['state']-1;
		} 
		mysql_close($con);
	} 
} 

?>
