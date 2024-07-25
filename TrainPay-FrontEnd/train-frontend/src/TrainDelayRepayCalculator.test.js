// TrainDelayRepayCalculator.test.js
import React from 'react';
import { render, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import TrainDelayRepayCalculator from './TrainDelayRepayCalculator';

describe('TrainDelayRepayCalculator Component', () => {
    test('renders the heading and subtext correctly', () => {
        const { getByText } = render(<TrainDelayRepayCalculator />);

        // Check for the heading
        expect(getByText('Train Delay Repay Calculator')).toBeInTheDocument();

        // Check for the subtext
        expect(getByText('Everyday trains run late')).toBeInTheDocument();
    });

    test('renders image preview box and buttons', () => {
        const { getByText, getByRole } = render(<TrainDelayRepayCalculator />);

        // Check for the image preview placeholder
        expect(getByText('Image Preview')).toBeInTheDocument();

        // Check for the buttons
        expect(getByRole('button', { name: /upload image/i })).toBeInTheDocument();
        expect(getByRole('button', { name: /suggest updates/i })).toBeInTheDocument();
        expect(getByRole('button', { name: /donate/i })).toBeInTheDocument();
    });

    test('renders the table with correct headers', () => {
        const { getByText } = render(<TrainDelayRepayCalculator />);

        // Check for the table headers
        expect(getByText('Train Provider')).toBeInTheDocument();
        expect(getByText('Train Time')).toBeInTheDocument();
        expect(getByText('Amount Late')).toBeInTheDocument();
        expect(getByText('Possible Claim')).toBeInTheDocument();
    });

    test('simulates image upload and preview', () => {
        const { getByText, getByRole, getByAltText } = render(<TrainDelayRepayCalculator />);

        // Mock the file input
        const fileInput = getByRole('button', { name: /upload image/i }).parentNode.querySelector('input[type="file"]');

        const file = new File(['hello'], 'hello.png', { type: 'image/png' });

        // Simulate file upload
        fireEvent.change(fileInput, { target: { files: [file] } });

        // Check if the image is displayed
        expect(getByAltText('Selected')).toBeInTheDocument();
    });

    test('handles button clicks', () => {
        const { getByRole } = render(<TrainDelayRepayCalculator />);

        // Check the file input is triggered on button click
        const uploadButton = getByRole('button', { name: /upload image/i });
        const fileInput = uploadButton.parentNode.querySelector('input[type="file"]');

        // Mock the click event
        const fileInputClickSpy = jest.spyOn(fileInput, 'click');

        // Trigger the click
        fireEvent.click(uploadButton);

        // Expect the file input click event to be triggered
        expect(fileInputClickSpy).toHaveBeenCalled();
    });
});
