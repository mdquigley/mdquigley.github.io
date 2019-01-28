%       Name: Michael Quigley
%
%   Fall 2011
%   FUND. DST
%   Dr. Roginska
%
% FINAL PROJECT - Ring Modulator & STFT
%
% Example of how to run function with supplied audio file 'poem.wav' :
%
%       ringmod('poem', 30, 9, 100, 10, 'hamming', 'ringPoem')
%
%--------------------------------------------------------------------------
%
% Help:
%
% ringmod(signal, fCarrier, aCarrier, winLength, overLapLength, winType, OUTfile)
%
%   ringmod takes in variables for a modulator signal, carrier frequency
%   and amplitude, window length, overlap length, window type, and output
%   filename. It "ring modulates" the modulator signal and carrier
%   sine wave, performs a Short-Time Fourier Transform, and outputs the
%   resultant audio to a .WAV file, graphs the frequency and amplitude
%   data, and sounds the output audio.
%
% Inputs:
%   signal - filename of input .WAV modulator
%   fCarrier - frequency of carrier sine wave (in Hertz)
%   aCarrier - amplitude of carrier sine wave
%   winLength - length of the STFT window (in samples)
%   overLapLength - length of STFT window overlap (in samples)
%   winType - window type,must be one of the following strings:
%               - 'rect'
%               - 'hamming'
%               - 'hann'
%               - 'blackman'
%               - 'bartlett'
%   OUTfile - filename of output .WAV file to be written
%
% Output:
%   1) A .WAV file of the ring modulated audio, of filename designated by
%   OUTfile. 2) Audio playback of the result. 3)A graph of the frequency
%   content vs. amplitude content of the result.
%
%


function ringmod(signal, fCarrier, aCarrier, winLength, overLapLength, winType, OUTfile)
tic
%CHECKS

if nargin ~= 7 % checks there are 7 inputs
    error('You must supply all seven inputs.');
end

if fCarrier < 1 || mod(fCarrier, 1) ~= 0 % checks fCarrier is valid
    error('fCarrier must be a positive integer.');
end

if winLength < 1 || rem(winLength,1) ~= 0 % checks winLength is valid.
    error('winLength must be a positive integer value.');
end

if overLapLength < 1 || rem(overLapLength,1) ~= 0 % checks overLapLength is valid.
    error('overLapLength must be a positive integer value.');
end

if winLength < overLapLength % checks winLength is larger than overLapLength.
    error('winLength must be larger than the overLapLength')
end

if ischar(winType) == 0 % checks winType is a string.
    error('winType must be one of the following: rect, hamming, hann, blackman, or bartlett.')
end

if ischar(OUTfile) == 0 % checks OUTfile is a valid string
    error('OUTfile must be a string of characters.');
end

%Ring Modulation

[signal, fs, nBits] = wavread(signal); % read in signal

[nSamps, nChannels] = size(signal);  % length of signal and # of channels

if nChannels > 1 % monos signal if stereo
    signal = mean(signal, 2);
end

t = (0:nSamps-1)'/fs; % generates time vector for carrier

carrier =  aCarrier * sin(2*pi*fCarrier*t); % generates carrier sine wave

output = signal .* carrier; % multiplies carrier with signal

output = output ./ abs(max(output)) .* 0.9; % normalizes output

wavwrite(output, fs, nBits, OUTfile); % writes ouput to .WAV file

% STFT

hopSize = winLength - overLapLength; % calcs hop size

outputPad = [output; zeros(winLength, 1)]; % zero pad end of output


% sets window type
switch winType
    case 'rect'
        window = rectwin(winLength);

    case 'hamming'
        window = hamming(winLength);

    case 'hann'
        window = hann(winLength);

    case 'blackman'
        window = blackman(winLength);

    case 'bartlett'
        window = bartlett(winLength);

    otherwise
        error('winType must be one of the following: rect, hamming, hann, blackman, bartlett.')
end

halfWindow = winLength/2; % sets half window length

y = zeros(halfWindow, ceil(nSamps/hopSize)); % creates empty matrix for fft

column = 1; % initialize column start point


for k = 1:hopSize:nSamps

    % applies window to output section
    X = fft(window .* outputPad(k : k + winLength - 1));

    % fills fft data into bin
    y(:, column) = X(1:halfWindow);

    % moves to next bin
    column = column + 1;

end

% calc normalized magnitudes of fft data
MAG = abs(y)/sum(window);

freqs = (0:length(MAG)-1) * (fs/length(MAG)); % frequency vector for fft graph
plot(freqs, MAG); % graphs frequency and amplitude data of ring-modulated output
xlabel('frequency'); % x axis label
ylabel('magnitude'); % y axis label
title('Frequency vs. Magnitude of Output'); % graph title
sound(output, fs, nBits); % playback of ring-mod output
toc
end
