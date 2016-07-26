<?php
$imgdir = "./upload_img/";
$filetype = ".png";
$isfinished = false;
if ($_FILES["file"]["error"] > 0) {
	echo "ERROR";
} else {
	$datenow = date("YmdHis");
	echo $datenow;
	$imgname = $datenow;
	$isfinished = move_uploaded_file($_FILES["file"]["tmp_name"], $imgdir . $imgname . $filetype);
	chmod($imgdir . $imgname . $filetype,0777);
	if ($isfinished == true) {
		echo true;
	} else {
		echo false;
	} 
} 
?>