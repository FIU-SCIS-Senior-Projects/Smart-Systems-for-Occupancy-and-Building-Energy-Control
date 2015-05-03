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
$sql = "SELECT * FROM testing_ac_prediction WHERE date = '".$date."'";

$result = mysqli_query($conn, $sql);

$list = array();

if (mysqli_num_rows($result) > 0) {
	
    // output data of each row
    while($row = mysqli_fetch_assoc($result)) {
		$list[] = array('outside_temperature' => $row["outside_temperature"], 
						'ac_energy_usage' => $row["ac_energy_usage"],
						'ac_setpoint' => $row["ac_setpoint"]);
    }
	
	echo json_encode($list, JSON_FORCE_OBJECT);
	
} else {
    echo "No Data";
}
mysqli_close($conn);
?>