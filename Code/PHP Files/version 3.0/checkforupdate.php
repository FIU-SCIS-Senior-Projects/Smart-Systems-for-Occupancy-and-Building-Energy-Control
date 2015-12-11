<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$region_id = $_POST['region_id'];
$zone_temperature = $_POST['zone_temperature'];
$zone_lighting = $_POST['zone_lighting'];
$zone_occupancy = $_POST['zone_occupancy'];
$zone_plugload = $_POST['zone_plugload'];

$sql = "SELECT COUNT(*) FROM zone_temperature WHERE zone_description_region_id IN ".$region_id."";
$result = mysqli_query($conn, $sql);

$change = false;

	if($zone_temperature != $result)
		{$change = true;}

$sql = "SELECT COUNT(*) FROM zone_lighting WHERE zone_description_region_id IN ".$region_id."";
$result = mysqli_query($conn, $sql);	

	if($zone_lighting != $result)
		{$change = true;}

$sql = "SELECT COUNT(*) FROM zone_occupancy WHERE zone_description_region_id IN ".$region_id."";
$result = mysqli_query($conn, $sql);
	
	if($zone_occupancy != $result)
		{$change = true;}

$sql = "SELECT COUNT(*) FROM zone_plugload WHERE zone_description_region_id IN ".$region_id."";
$result = mysqli_query($conn, $sql);
	
	if($zone_plugload != $result)
		{$change = true;}
	
echo $change;
//echo $change . "Result: " . $result . ", Zone PlugLoad: ". $zone_plugload;

mysqli_close($conn);
?>
