import React, { useState, useEffect } from 'react';
import { View, Text, Button, Alert, StyleSheet } from 'react-native';
import ReactNativeBiometrics from 'react-native-biometrics';
import { NativeModules } from 'react-native';
const { FingerprintScannerModule } = NativeModules;

const handleSubmit = async (userId, encodedFingerprintData) => {
  const payload = { userId, encodedFingerprintData };

  try {
    const response = await fetch('http://ustech-001-site22.mtempurl.com/api/Fingerprint/VerifyAndMarkAttendance', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });

    const responseData = await response.json();

    if (response.ok) {
      Alert.alert('Success', 'Data posted successfully');
    } else {
      throw new Error(responseData.message || 'Something went wrong');
    }
  } catch (error) {
    console.error('Error posting data:', error);
    Alert.alert('Error', 'Failed to post data');
  }
};

const Attendance = () => {
  const [authenticated, setAuthenticated] = useState(false);
  const markAttendance = async () => {
    FingerprintScannerModule.startFingerprintScan(
        (message) => {
            console.log('Fingerprint captured successfully:', message);
            // Call Attendance API
            handleSubmit("", message)
            Alert.alert('Success', 'User ID: ' + message)
        },
        (error) => {
            console.error('Fingerprint scan error:', error);
        }
      );
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Attendance</Text>
      <Button title="Mark Attendance" onPress={markAttendance} />
      {authenticated && <Text>Attendance successfully marked</Text>}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  title: {
    fontSize: 20,
    marginBottom: 20,
  },
});

export default Attendance;
