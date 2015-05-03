<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$sql = "SELECT `zone_description_region_id`, SUM(appliance_time_plugged) FROM plugload_energy_performance GROUP BY `zone_description_region_id` ORDER BY SUM(appliance_time_plugged)  ASC";

$result = mysqli_query($conn, $sql);
$list = array();

if (mysqli_num_rows($result) > 0) {
    // output data of each row
    while($row = mysqli_fetch_assoc($result)) {
                $list[] = array('zone_description_region_id' => $row["zone_description_region_id"], 'appliance_time_plugged' => $row["SUM(appliance_time_plugged)"]);
    }

        echo json_encode($list, JSON_FORCE_OBJECT);

} else {
    echo $sql;
}
mysqli_close($conn);
?>
