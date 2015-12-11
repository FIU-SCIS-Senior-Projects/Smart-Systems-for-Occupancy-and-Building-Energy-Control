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
$ligh_tot = $_POST['lighting_total_kw'];
$light_waste = $_POST['lighting_waste_kw'];

$sql = "INSERT INTO `lighting_energy_performance`(`date`, `zone_description_region_id`, `lighting_total_kw`, `lighting_waste_kw`)".
		" VALUES ('".$date."',".$id.",".$ligh_tot.",".$light_waste.")";
				
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
