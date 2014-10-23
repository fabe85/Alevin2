      /* sets */
      set SNnodes;
      set SNlinks within (SNnodes cross SNnodes);
      set SNUnmappedLinks within SNlinks;
      set VNnodes within SNnodes;
      set VNlinks within (VNnodes cross VNnodes);

      /* parameters */
      param LinkCapacity {(i,j) in SNlinks};
      param SNodeCapacity {i in SNnodes};
      param VLinkDemand {(k,l) in VNlinks};
      param HHDemand {(k,l) in VNlinks};
      param w1;
      param w2;
      /* decision variables: "binary relaxed lambda Variables"*/
      var lambda {VNlinks cross SNlinks} >= 0, <=1;

      /* objective function */
      minimize z: sum{(i,j) in SNUnmappedLinks} sum{(k,l) in VNlinks} lambda[k,l,i,j];

      /* Constraints */
      s.t. SourceI{(k,l) in VNlinks}: sum{(k,j) in SNlinks} lambda[k,l,k,j] =1;
      s.t. SourceII{(k,l) in VNlinks}: sum{(i,k) in SNlinks} lambda[k,l,i,k] =0;
      s.t. DestI{(k,l) in VNlinks}: sum{(i,l) in SNlinks} lambda[k,l,i,l] =1;
      s.t. DestII{(k,l) in VNlinks}: sum{(l,j) in SNlinks} lambda[k,l,l,j] =0;
      s.t. Flowcon{h in SNnodes, (k,l) in VNlinks}: sum{(h,j) in SNlinks} (if (h<>k and h<>l) then lambda[k,l,h,j] else 0) - sum{(i,h) in SNlinks} (if (h<>k and h<>l) then lambda[k,l,i,h] else 0) = 0;
      s.t.  CapacityLinkDem{(i,j) in SNlinks} : sum{(k,l) in VNlinks} lambda[k,l,i,j]*VLinkDemand[k,l] <= LinkCapacity[i,j];
      s.t.  CapacityNodeDem{h in SNnodes} : sum{(k,l) in VNlinks}sum{(h,j) in SNlinks}(if(k<>h and h<>l) then lambda[k,l,h,j]*HHDemand[k,l] else 0)<= SNodeCapacity[h];
      s.t.  DomainConstr{(k,l,i,j) in VNlinks cross SNlinks} : 0 <= lambda[k,l,i,j] <= 1;
