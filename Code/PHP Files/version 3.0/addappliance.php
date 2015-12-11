<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$zone_description_region_id = $_POST['zone_description_region_id'];
$appliance_name = $_POST['appliance_name'];
$appliance_type = $_POST['appliance_type'];
$status = $_POST['status'];
$energy_usage_kwh = $_POST['energy_usage_kwh'];

$sql = "INSERT INTO zone_plugload(zone_description_region_id, time_stamp, appliance_name, appliance_type, status, energy_usage_kwh) VALUES (".$zone_description_region_id.", NULL ,'".$appliance_name. "','" .$appliance_type."','" .$status. "'," .$energy_usage_kwh.")";
$result = mysqli_query($conn, $sql);

if($result) // Check if insert statement was successful
{
    //Show success message
    echo 'Successful';
}else{
    //Show error message
    echo 'Mysql Error: '. mysqli_error($conn);
}


mysqli_close($conn);
?>
