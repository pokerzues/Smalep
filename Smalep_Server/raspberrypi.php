<?php

function _get($str) {
	$val = !empty($_GET[$str]) ? $_GET[$str] : null;
	return $val;
} 
if (_get('token') == "sp199539") {
	$Temp = _get('Temp');
	$Humidity = _get('Humidity');
	$LightLevel = _get('LightLevel');
	$Urgent = _get('Urgent');
	if ($Temp == 0)$Temp = 1;
	if ($Humidity == 0)$Humidity = 1;
	$Urgent = $Urgent + 1;

	$mysql_server_name = "localhost";
	$mysql_username = "root";
	$mysql_password = "ede1954b";

	$con = mysql_connect($mysql_server_name, $mysql_username, $mysql_password)or die("Connect failed" . mysql_error());

	mysql_select_db("server", $con)or die("connect failed" . mysql_error());
	mysql_query("SET NAMES utf8");

	$datenow = date("Y/m/d H:i:s");
	$sql = "UPDATE sensor SET val='$Temp' WHERE id = '0'";
	mysql_query($sql);
	$sql = "UPDATE sensor SET timestamp='$datenow' WHERE id = '0'";
	mysql_query($sql);

	$sql = "UPDATE sensor SET val='$Humidity' WHERE id = '1'";
	mysql_query($sql);
	$sql = "UPDATE sensor SET timestamp='$datenow' WHERE id = '1'";
	mysql_query($sql);

	$sql = "UPDATE sensor SET val='$LightLevel' WHERE id = '2'";
	mysql_query($sql);
	$sql = "UPDATE sensor SET timestamp='$datenow' WHERE id = '2'";
	mysql_query($sql);

	$sql1 = "UPDATE ugt SET state='$Urgent' WHERE name = '0'";
	mysql_query($sql1);
	$sql1 = "UPDATE ugt SET timestamp='$datenow' WHERE name = '0'";
	mysql_query($sql1);

	$sql = "SELECT val FROM cmd WHERE id = 0";
	$result = mysql_query($sql);
	while ($row = mysql_fetch_array($result)) {
		$cmd0 = $row['val']-1;
	} 
	$sql = "SELECT val FROM cmd WHERE id = 1";
	$result = mysql_query($sql);
	while ($row = mysql_fetch_array($result)) {
		$cmd1 = $row['val']-1;
	} 
	$sql = "SELECT val FROM cmd WHERE id = 2";
	$result = mysql_query($sql);
	while ($row = mysql_fetch_array($result)) {
		$cmd2 = $row['val']-1;
	} 
	$sql = "SELECT val FROM cmd WHERE id = 3";
	$result = mysql_query($sql);
	while ($row = mysql_fetch_array($result)) {
		$cmd3 = $row['val']-1;
	} 
	$sql = "SELECT val FROM cmd WHERE id = 4";
	$result = mysql_query($sql);
	while ($row = mysql_fetch_array($result)) {
		$cmd4 = $row['val']-1;
	} 
	$sql = "SELECT val FROM cmd WHERE id = 5";
	$result = mysql_query($sql);
	while ($row = mysql_fetch_array($result)) {
		$cmd5 = $row['val']-1;
	} 

	mysql_close($con);

	echo "{" . $cmd0 . "+" . $cmd1 . "+" . $cmd2 . "+" . $cmd3 . "+" . $cmd4 . "+" . $cmd5 . "}";

	if ($Urgent == 2) {
		require_once './sdk.php';
		$sdk = new PushSDK();
		$channelId = '3548658424474747953';
		$tt=iconv('gb2312', 'utf-8', 'Smalep：请注意');
		$desc=iconv('gb2312', 'utf-8', '检测到陌生人，请及时进行检查');
		$message = array (
			'title' => $tt,
			'description' => $desc
			);
		$opts = array ('msg_type' => 1
			);
		$rs = $sdk -> pushMsgToSingleDevice($channelId, $message, $opts);
	} 
} 

?>
