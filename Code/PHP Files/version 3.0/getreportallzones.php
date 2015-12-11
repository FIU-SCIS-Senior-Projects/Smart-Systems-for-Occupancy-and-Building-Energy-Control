<?php


// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}


//get regions by plugload
$sql = "SELECT `region_name`, SUM(appliance_time_plugged) AS plugload FROM `plugload_energy_performance`, `zone_description` WHERE `zone_description_region_id` = `region_id` GROUP BY `zone_description_region_id` ORDER BY `plugload` DESC";

$result = mysqli_query($conn, $sql);

//get regions 
//$sql2 = "SELECT region_name, status FROM (SELECT zone_description_region_id AS id1, MAX(time_stamp), status FROM zone_lighting GROUP BY zone_description_region_id) AS t1, (SELECT zone_description_region_id AS id2, MAX(time_stamp), occupancy FROM zone_occupancy GROUP BY zone_description_region_id) AS t2, zone_description WHERE status = 'ON' AND occupancy = 0 AND id1 = id2 AND id1 = region_id"; 
//$res2 = mysqli_query($conn, $sql2);


$list = array();

//if (mysqli_num_rows($res2) > 0) {
  // while($row = mysqli_fetch_assoc($res2)){
//	$list[] = array('region_name' => $row["region_name"], 'description' => $row["status"], 'plugload' => null);

//   }
//}



if (mysqli_num_rows($result) > 0) {
    // output data of each row
    
    while($row = mysqli_fetch_assoc($result)) {
        $counter ++;
        //echo "=== " . $row["region_name"] . " [[[[[ " . $row["plugload"] . "\n";
	$list[] = array('region_name' => $row["region_name"], 'description' => "zone", 'plugload' => $row["plugload"]);
    }

	echo json_encode($list, JSON_FORCE_OBJECT);

}

else {
    echo 'MySQL Error: '. mysqli_error($conn);
}
mysqli_close($conn);
?>
