<?php


// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$userId = $_POST['user_id'];
//echo "id " . $_POST['user_id'];
//get regions by plugload
$sql = "SELECT `region_name`, SUM(appliance_time_plugged) AS plugload FROM `plugload_energy_performance`, `zone_description`,(SELECT zone_description_region_id AS region FROM `region_authority` WHERE user_user_id = '". $userId ."' ) AS t1 WHERE `zone_description_region_id` = `region_id` AND `zone_description_region_id` = t1.region GROUP BY `zone_description_region_id` ORDER BY `plugload` DESC";
//echo $sql;
$result = mysqli_query($conn, $sql);

$list = array();


if (mysqli_num_rows($result) > 0) {
    // output data of each row
    
    while($row = mysqli_fetch_assoc($result)) {
        
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
