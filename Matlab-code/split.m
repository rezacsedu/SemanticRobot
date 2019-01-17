function varargout = split(data, perc, seed)
% Randomly splits data into length(perc) subsets with size perc.
% Splits over columns if perc is row vector. Splits over rows if perc is column vector.
%
% EXAMPLE: Split columns into 80% traindata, 10% validation data and 10% testdata
%           [traindata, valdata, testdata] = split(data, [0.8 0.1 0.1])

if sum(perc)~=1
    error('Sum of perc is not 1');
end

[~, orientation] = max(size(perc));

if nargin==3
    rand('state', seed)
end

S=length(perc); %number of subsets
%vargout = cell(S,1);
N=size(data, orientation);
k=randperm(N);

perc = [0; perc(:)];

for i=1:S
    if orientation==1
        varargout{i} = data(k(1+floor(N*sum(perc(1:i))):floor(N*sum(perc(1:i+1)))),:);
    else
        varargout{i} = data(:,k(1+floor(N*sum(perc(1:i))):floor(N*sum(perc(1:i+1)))));
    end
end

end