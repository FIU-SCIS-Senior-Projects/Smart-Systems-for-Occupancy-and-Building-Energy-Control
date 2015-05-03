<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$date = $_POST['date'];
$id = $_POST['zone_description_region_id'];
$appl_time = $_POST['appliance_time_plugged'];
$appl_type = $_POST['appliance_type'];

$sql = "INSERT INTO `plugload_energy_performance`(`date`, `zone_description_region_id`, `appliance_time_plugged`, `appliance_type`)".
		" VALUES ('".$date."',".$id.",".$appl_time.",'".$appl_type."')";

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