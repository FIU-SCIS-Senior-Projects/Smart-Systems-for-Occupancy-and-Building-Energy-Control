<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$sql = "SELECT `appliance_type`, AVG(appliance_time_plugged) FROM plugload_energy_performance GROUP         BY `appliance_type`";

$result = mysqli_query($conn, $sql);


$list = array();

if (mysqli_num_rows($result) > 0) {
    // output data of each row
    while($row = mysqli_fetch_assoc($result)) {
                $list[] = array('appliance_type' => $row["appliance_type"], 'appliance_time_plugged'         => $row["AVG(appliance_time_plugged)"]);
    }

        echo json_encode($list, JSON_FORCE_OBJECT);

}else{
    //Show error message
    echo 'Mysql Error: '. mysqli_error($conn);
}

mysqli_close($conn);
?>
