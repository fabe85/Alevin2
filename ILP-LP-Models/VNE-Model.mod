      #
      # VNE problem
      #
      # This finds the optimal solution to the optimal LP relaxation of the VNE problem
      #

      /* sets */
      set SNnodes;
      set SNlinks within (SNnodes cross SNnodes);
      set VNnodes within SNnodes;
      set VNlinks within (VNnodes cross VNnodes);

      /* parameters */
      param LinkCapacity {(i,j) in SNlinks};
      param VLinkDemand {(k,l) in VNlinks};
      param w1;
      param w2;
      /* decision variables: "binary relaxed lambda Variables"*/
      var lambda {VNlinks cross SNlinks} >= 0, <=1;

      /* objective function */
      minimize z: w1*(sum{(i,j) in SNlinks}sum{(k,l) in VNlinks} lambda[k,l,i,j]*VLinkDemand[k,l]);

      /* Constraints */
      s.t. SourceI{(k,l) in VNlinks}: sum{(i,j) in SNlinks} (if k=i then lambda[k,l,i,j] else 0) =1;
      s.t. SourceII{(k,l) in VNlinks}: sum{(i,j) in SNlinks} (if k=j then lambda[k,l,i,j] else 0) =0;
      s.t. DestI{(k,l) in VNlinks}: sum{(i,j) in SNlinks} (if l=j then lambda[k,l,i,j] else 0) =1;
      s.t. DestII{(k,l) in VNlinks}: sum{(i,j) in SNlinks} (if l=i then lambda[k,l,i,j] else 0) =0;
      s.t. Flowcon{h in SNnodes, (k,l) in VNlinks}: sum{(i,j) in SNlinks} (if (h=i and h<>k and h<>l) then lambda[k,l,i,j] else 0) - sum{(i,j) in SNlinks} (if (h=j and h<>k and h<>l) then lambda[k,l,i,j] else 0) = 0;
      s.t. CapacityLinkDem{(i,j) in SNlinks} : sum{(k,l) in VNlinks} lambda[k,l,i,j]*VLinkDemand[k,l] <= LinkCapacity[i,j];
      s.t. DomainConstr{(k,l,i,j) in VNlinks cross SNlinks} : 0 <= lambda[k,l,i,j] <= 1;
