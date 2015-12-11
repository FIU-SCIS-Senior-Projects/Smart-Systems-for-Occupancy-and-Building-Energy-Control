<?php
// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$sql = "SELECT * FROM zone_description";

$result = mysqli_query($conn, $sql);

$list = array();

if (mysqli_num_rows($result) > 0) {
	
    // output data of each row
    while($row = mysqli_fetch_assoc($result)) {
		
		$list[] = array('region_id' => $row["region_id"], 
						'region_name' => $row["region_name"],
						'location' => $row["location"],
						'windows' =>$row["windows"]);
    }
	
	echo json_encode($list, JSON_FORCE_OBJECT);
	
} else {
    echo "No Data";
}
mysqli_close($conn);
?>
