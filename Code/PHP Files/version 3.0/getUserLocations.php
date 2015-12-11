<?php


// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$sql = "select * from user where latitude IS NOT NULL AND longitude IS NOT NULL";
$result = mysqli_query($conn, $sql);


if (mysqli_num_rows($result) > 0) {
    // output data of each row
    while($row = mysqli_fetch_assoc($result)) {
        echo "id:" . $row["user_id"]. ":name:" . $row["name"]. ":latitude:" . $row["latitude"]. ":longitude:" .$row["longitude"]. ":rewards:" .$row["rewards_total"]. "";
    }
} else {
    echo "Not A User";
}
mysqli_close($conn);
?>
