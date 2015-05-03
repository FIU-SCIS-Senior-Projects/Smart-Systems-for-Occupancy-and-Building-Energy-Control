<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

//array of region id's
$region_id = json_decode($_POST['region_id'], true);

//last time_stamp imformation for each region id
$last_time_stamp = json_decode($_POST['last_time_stamp'], true);


$sql = "SELECT * FROM zone_plugload WHERE";

for($i = 0; $i < count($region_id); $i++)
{
	$sql = $sql . " ((zone_description_region_id = ".$region_id[$i].") AND (time_stamp > '".$last_time_stamp[$i]."')) ";
	
	if($i != (count($region_id) - 1))
	{
		$sql = $sql . " OR ";
	}
}

$result = mysqli_query($conn, $sql);

$list = array();

if (mysqli_num_rows($result) > 0) {
	
    // output data of each row
    while($row = mysqli_fetch_assoc($result)) {
		$list[] = array('time_stamp' => $row["time_stamp"], 
						'appliance_name' => $row["appliance_name"], 
						'zone_description_region_id' => $row["zone_description_region_id"],
						'energy_usage_kwh' => $row["energy_usage_kwh"], 
						'appliance_type' => $row["appliance_type"],  
						'status' => $row["status"]);
    }
	
	echo json_encode($list, JSON_FORCE_OBJECT);
	
} else {
    echo "No Data";
}
mysqli_close($conn);
?>