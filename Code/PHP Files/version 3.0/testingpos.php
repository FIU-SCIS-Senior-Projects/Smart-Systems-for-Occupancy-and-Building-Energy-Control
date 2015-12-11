<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$region_id = 1;
$sql = "SELECT * FROM zone_occupancy WHERE zone_description_region_id = ".$region_id."";

$result = mysqli_query($conn, $sql);

//$arr = array('a' => 1, 'b' => 2, 'c' => 3, 'd' => 4, 'e' => 5);
$list = array();

//example of a JSON output  { "occupancy": [ { \"time_stamp\": \"2015-02-13 09:13:33\", \"occupancy\": 2 },   
//																	{ \"time_stamp\": \"2015-02-14 09:13:33\", \"occupancy\": 0 } ] }"
if (mysqli_num_rows($result) > 0) {
    // output data of each row
    while($row = mysqli_fetch_assoc($result)) {
        //$con .= " { \"time_stamp\": \"".$row["time_stamp"]."\", \"occupancy\": ".$row["occupancy"]." }, ";
		
		$list[] = array('timestamp' => $row["time_stamp"], 'occupancy' => $row["occupancy"]);
    }
	
	echo json_encode($list, JSON_FORCE_OBJECT);
} else {
    echo "No Data";
}
mysqli_close($conn);
?>
