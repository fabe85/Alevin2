      #
      # VNE problem
      #
      # This finds the optimal solution to the optimal LP relaxation of the MIP VNE problem for the node mapping phase
      #

      /* sets */
      set SNnodes;
      set SNnodesOriginal within SNnodes;
      set SNlinks within (SNnodes cross SNnodes);
      set SNlinksOriginal within SNlinks;
      set VNnodes within SNnodes;
      set VNlinks within (VNnodes cross VNnodes);

      /* parameters */
      param LinkCapacity {(i,j) in SNlinks};
      param SNodeCapacity {i in SNnodes};
      param VLinkDemand {(k,l) in VNlinks};
      param VNodeDemand {i in VNnodes};
      param w1;
      param w2;
      param w3;
      param HHFactor;
      /* decision variables: "binary relaxed lambda Variables"*/
      var lambda {SNnodes cross SNnodes} >= 0, <=1;
      var flow {VNlinks cross SNlinks} >= 0;

      /* objective function */
      minimize z: sum{(i,j) in SNlinksOriginal}(w1/(LinkCapacity[i,j]+w3))*(sum{(k,l) in VNlinks} flow[k,l,i,j]) + sum{w in SNnodesOriginal}(w2/(SNodeCapacity[w]+w3))*((sum{m in VNnodes}lambda[m,w]*VNodeDemand[m]) + (sum{(k,l) in VNlinks}sum{(i,w) in SNlinksOriginal} (if ((w,l) in SNlinks) then 0 else flow[k,l,i,w]*HHFactor)));

      /* Constraints */
      s.t. CapacityLinkDem{(i,j) in SNlinks} : sum{(k,l) in VNlinks} flow[k,l,i,j] <= LinkCapacity[i,j]*lambda[i,j];
      s.t. CapacityNodeDemI{w in SNnodesOriginal} : SNodeCapacity[w] >= sum{m in VNnodes}(lambda[m,w]*VNodeDemand[m]) + sum{(k,l) in VNlinks}sum{(i,w) in SNlinksOriginal} (if((w,l) in SNlinks) then 0 else flow[k,l,i,w]*HHFactor);
      /* Not in the real model, adjusted because of Directed graph */
      /***********/
      /* Not in the real model, adjusted because of Directed graph (changed)*/
      s.t. SourceI{(k,l) in VNlinks}: sum{(k,j) in SNlinks} flow[k,l,k,j] = VLinkDemand[k,l];
      s.t. SourceII{(k,l) in VNlinks}: sum{(i,k) in SNlinks} flow[k,l,i,k] = 0;
      /**********Constraint to avoid going from one node to the same node in the augmented substrate (Not in the original model)****************/
      s.t. SourceIII{(k,l) in VNlinks, h in SNnodesOriginal}:  sum{(i,h) in SNlinks} (if k=i then flow[k,l,i,h] else 0) - sum{(h,j) in SNlinksOriginal} (if ((k,h) in SNlinks) then flow[k,l,h,j] else 0) = 0;
      /**********************************************************************************************************/
      s.t. DestI{(k,l) in VNlinks}: sum{(i,l) in SNlinks} flow[k,l,i,l] = VLinkDemand[k,l];
      s.t. DestII{(k,l) in VNlinks}: sum{(l,j) in SNlinks} flow[k,l,l,j] = 0;
      s.t. NoHHI{m in VNnodes, (k,l) in VNlinks}: sum{(m,j) in SNlinks} (if m<>k then flow[k,l,m,j] else 0) = 0;
      s.t. NoHHII{m in VNnodes, (k,l) in VNlinks}: sum{(i,m) in SNlinks} (if m<>l then flow[k,l,i,m] else 0) = 0;
      s.t. Flowcon{h in SNnodesOriginal, (k,l) in VNlinks}: sum{(h,j) in SNlinks} (if (h<>k and h<>l) then flow[k,l,h,j] else 0) - sum{(i,h) in SNlinks} (if (h<>k and h<>l) then flow[k,l,i,h] else 0) = 0;
      /**********************/
      s.t.  MetaConstraintI{m in VNnodes}: sum{(m,j) in SNlinks} lambda[m,j] = 1;
      s.t.  MetaConstraintII{w in SNnodesOriginal}: sum{m in VNnodes}lambda[m,w]  <= 1;
      s.t.  MetaConstraintIII{(i,j) in SNlinks}: lambda[i,j] <= LinkCapacity[i,j];
